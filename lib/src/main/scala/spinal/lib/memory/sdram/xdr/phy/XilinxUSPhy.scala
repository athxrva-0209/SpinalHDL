package spinal.lib.memory.sdram.xdr.phy

import spinal.core._
import spinal.lib._
import spinal.lib.blackbox.xilinx.ultrascale.{IDELAYCTRL, IDELAYE3, IOBUFDS, IOBUF, ISERDESE3, MMCME3_ADV, OBUFDS, ODELAYE3, OSERDESE3}
import spinal.lib.bus.misc.BusSlaveFactory
import spinal.lib.memory.sdram.SdramLayout
import spinal.lib.memory.sdram.xdr.{PhyLayout, SdramXdrIo, SdramXdrPhyCtrl}

import scala.collection.Seq
import spinal.core.sim._
import spinal.lib.memory.sdram.SdramGeneration.DDR3
import spinal.lib.memory.sdram._



object XilinxUSPhy{
  def phyLayout(sl : SdramLayout, clkRatio : Int) = PhyLayout(
    sdram = sl,
    phaseCount = clkRatio,
    dataRate = 2,
    outputLatency = 2,
    readDelay = 0,
    writeDelay = 0,
    cmdToDqDelayDelta = 1,
    transferPerBurst = Math.max(4, sl.generation.burstLength)
  )
}

case class XilinxUSPhy(sl : SdramLayout,
                       clkRatio : Int,
                       clk90 : ClockDomain,
                       serdesClk0 : ClockDomain,
                       serdesClk90 : ClockDomain) extends Component {
  val pl = XilinxUSPhy.phyLayout(sl, clkRatio)

  val io = new Bundle {
    val ctrl = slave(SdramXdrPhyCtrl(pl))
    val sdram = master(SdramXdrIo(sl))
  }

  assert(clkRatio == 2)
  val phaseCount = clkRatio

  val clk270Rst = ResetCtrl.asyncAssertSyncDeassert(
    input = ClockDomain.current.isResetActive,
    clockDomain = clk90.withRevertedClockEdge(),
    inputPolarity = HIGH,
    outputPolarity = HIGH
  )
  val clk270 = clk90.withRevertedClockEdge().copy(reset = clk270Rst)

  val clk90Rst = ResetCtrl.asyncAssertSyncDeassert(
    input = clk270Rst,
    clockDomain = clk90,
    inputPolarity = HIGH,
    outputPolarity = HIGH
  )

  //  clk270.setSynchronousWith(ClockDomain.current)

  val idelayctrl = IDELAYCTRL()
  idelayctrl.REFCLK := serdesClk90.readClockWire //TODO serdesClk90
  idelayctrl.RST := ClockDomain.isResetActive

  def valueToOutput(name: String, phase: Bool): Bool = seqToOutput(name, Seq.fill(4 * 2)(phase))._1

  def sdrToOutput(name: String, phases: Seq[Bool], outputEnable: Seq[Bool] = List.fill(phaseCount * 2)(True)): Bool = seqToOutput(name, phases.map(p => Seq.fill(2)(p)).flatten, outputEnable)._1

  def ddrToOutput(name: String, phases: Seq[Seq[Bool]], outputEnable: Seq[Bool] = List.fill(phaseCount * 2)(True)): Bool = seqToOutput(name, phases.flatten, outputEnable)._1

  def seqToOutput(name: String, seq: Seq[Bool], outputEnable: Seq[Bool] = List.fill(phaseCount * 2)(True), phase90: Boolean = false): (Bool, Bool) = {
    val serdes = OSERDESE3(
      DATA_WIDTH = phaseCount * 2,
      INIT = false,
      ODDR_MODE = "TRUE" // Set to TRUE to enable DDR mode.
    ).setName(s"${name}_OSERDESE3")

    // Concatenate the input sequence and assign it to the 8-bit D port.
    // Any unused bits of the D port will be padded with False.
    val dataWidth = phaseCount * 2
    serdes.D := (B(seq).resize(dataWidth) ## B(0, 8 - dataWidth bits)).resize(8)

    phase90 match {
      case false =>
        serdes.CLK := serdesClk0.readClockWire
        serdes.CLKDIV := ClockDomain.current.readClockWire
        serdes.RST := ClockDomain.current.isResetActive
      case true =>
        serdes.CLK := serdesClk90.readClockWire
        serdes.CLKDIV := clk90.readClockWire
        serdes.RST := clk90Rst
    }

    // OSERDESE3 has a single T input, so we use a logical OR of all `outputEnable` signals.
    serdes.T := !outputEnable.reduce(_ & _)
    (serdes.OQ, serdes.T_OUT) // Use T_OUT as the tristate output
  }

  val clkBuf = OBUFDS()
  clkBuf.I := seqToOutput("CK", Seq.fill(phaseCount)(Seq(True, False)).flatten, phase90 = true)._1
  io.sdram.CK := clkBuf.O
  io.sdram.CKn := clkBuf.OB

  // --- Command/Address/Control Pins (Write Path) ---
  for (i <- 0 until sl.chipAddressWidth) io.sdram.ADDR(i) := valueToOutput("ADDR", RegNext(io.ctrl.ADDR(i)))
  for (i <- 0 until sl.bankWidth) io.sdram.BA(i) := valueToOutput("BA", RegNext(io.ctrl.BA(i)))
  io.sdram.CASn := sdrToOutput("CASn", io.ctrl.phases.map(p => RegNext(p.CASn)))
  io.sdram.CKE := sdrToOutput("CKE", io.ctrl.phases.map(p => RegNext(p.CKE)))
  io.sdram.CSn := sdrToOutput("CSn", io.ctrl.phases.map(p => RegNext(p.CSn)))
  io.sdram.RASn := sdrToOutput("RASn", io.ctrl.phases.map(p => RegNext(p.RASn)))
  io.sdram.WEn := sdrToOutput("WEn", io.ctrl.phases.map(p => RegNext(p.WEn)))
  if (sl.generation.RESETn) io.sdram.RESETn := sdrToOutput("RESETn", io.ctrl.phases.map(p => RegNext(p.RESETn)))
  io.sdram.ODT := sdrToOutput("ODT", io.ctrl.phases.map(p => RegNext(p.ODT)))

  val idelayValueIn = in Bits (9 bits)

  val dqe0Reg = RegNext(io.ctrl.writeEnable) init (False)
  val dqe270Reg = clk270(RegNext(io.ctrl.writeEnable) init (False))
  val dqstReg = clk270(RegNext(Vec((io.ctrl.writeEnable ## dqe270Reg).mux(
    B"00" -> B"0000",
    B"10" -> B"0011",
    B"11" -> B"1111",
    B"01" -> B"1100"
  ).asBools.reverse)))
  dqstReg.foreach(_.init(False))

  val dqReg = io.ctrl.phases.map(p => RegNext(p.DQw))
  val dmReg = io.ctrl.phases.map(p => RegNext(p.DM))

  // --- DQS Write Path ---
  val dqs = for (i <- 0 until (sl.dataWidth + 7) / 8) yield new Area {
    val (serQ, serT) = seqToOutput("CK", Seq.fill(phaseCount)(Seq(True, False)).flatten, dqstReg, phase90 = true)
    val buf = IOBUFDS()
    buf.T := serT
    buf.I := serQ
    io.sdram.DQS(i) := buf.IO
    io.sdram.DQSn(i) := buf.IOB
  }

  // --- DM Write Path ---
  val dm = for (i <- 0 until (sl.dataWidth + 7) / 8) yield new Area {
    io.sdram.DM(i) := ddrToOutput("DM", dmReg.map(_.map(_(i))))
  }

  io.ctrl.readValid := io.ctrl.readEnable

  // --- DQ Write Path ---
  val dq = for (i <- 0 until sl.dataWidth) yield new Area {
    val buf = IOBUF()
    io.sdram.DQ(i) := buf.IO

    val (serQ, serT) = seqToOutput("DQ", dqReg.map(_.map(_(i))).flatten, List.fill(4)(dqe0Reg))
    buf.T := serT
    buf.I := serQ

    // --- Read Path (unimplemented) ---
  }



  case class PLLE2_ADV() extends BlackBox {
    addGeneric("CLKIN1_PERIOD", 10.0)
    addGeneric("CLKFBOUT_MULT", 8)
    addGeneric("CLKOUT0_DIVIDE", 4)
    addGeneric("CLKOUT0_PHASE", 0)
    addGeneric("CLKOUT1_DIVIDE", 2)
    addGeneric("CLKOUT1_PHASE", 90)

    val CLKFBIN = in Bool()
    val CLKIN1 = in Bool()
    val CLKFBOUT = out Bool()
    val CLKOUT0 = out Bool()
    val CLKOUT1 = out Bool()
  }
}

//sl = SdramLayout(dataWidth = 8, generation = DDR3, bankWidth = 3, columnWidth = 10, rowWidth = 14)

object XilinxUSPhyTest extends App {
  val simConfig = SimConfig.withFstWave.compile {
    new Component {
      val clk = in Bool()

      // --- 1. MMCM Instantiation and Clock Generation ---
      val mmcm = new MMCME3_ADV(
        CLKIN1_PERIOD = 10.0,
        DIVCLK_DIVIDE = 1,
        CLKFBOUT_MULT_F = 8.0,
        CLKOUT0_DIVIDE_F = 8.0,
        CLKOUT0_PHASE = 0.0,
        CLKOUT1_DIVIDE = 4,
        CLKOUT1_PHASE = 90.0,
        STARTUP_WAIT = "FALSE"
      )

      mmcm.CLKIN1 := clk
      mmcm.CLKIN2 := False
      mmcm.CLKINSEL := True
      mmcm.RST := False
      mmcm.CLKFBIN := mmcm.CLKOUT0
      mmcm.DADDR := 0
      mmcm.DCLK := False
      mmcm.DEN := False
      mmcm.DI := 0
      mmcm.DWE := False
      mmcm.PSCLK := False
      mmcm.PSEN := False
      mmcm.PSINCDEC := False
      mmcm.PWRDWN := False
      mmcm.CDDCREQ := False

      // --- 2. Clock Domain Setup ---
      // We are creating a separate clock domain for the 90-degree phase
      // by using the MMCM's CLKOUT1 output.
      val clk_dfi_domain = ClockDomain(
        clock = mmcm.CLKOUT0,
        reset = False
      )
      val clk90 = ClockDomain(
        clock = mmcm.CLKOUT1, // Explicitly use the 90-degree shifted clock
        reset = False
      )
      val clk_serdes0_domain = ClockDomain(
        clock = mmcm.CLKOUT0,
        reset = False
      )
      val clk_serdes90_domain = ClockDomain(
        clock = mmcm.CLKOUT1,
        reset = False
      )

      // --- 3. Instantiate the DUT (Device Under Test) ---
      val logic = new ClockingArea(clk_dfi_domain) {
        val phy = new XilinxUSPhy(
          sl = SdramLayout(dataWidth = 8, generation = DDR3, bankWidth = 3, columnWidth = 10, rowWidth = 14),
          clkRatio = 2,
          clk90 = clk90,
          serdesClk0 = clk_serdes0_domain,
          serdesClk90 = clk_serdes90_domain
        )
        val counter = Reg(UInt(8 bits)) init(0)
        counter := counter + 1

        phy.io.ctrl.phases(0).DQw := B(counter)
        phy.io.ctrl.phases(1).DQw := B(counter + 1)


        phy.io.ctrl.writeEnable := True

        phy.io.ctrl.phases.foreach { p =>
          p.CKE := True
          p.CSn := False
          p.RASn := True
          p.CASn := True
          p.WEn := True
        }
      }

      val sdram = logic.phy.io.sdram
    }.setDefinitionName("XilinxUSPhyTestbench")
  }

  // --- 4. Simulation Stimulus ---
  simConfig.doSim("XilinxUSPhyTest") { dut =>
    dut.clk #= false
    dut.clockDomain.forkStimulus(period = 10)

    dut.clockDomain.assertReset()
    dut.clockDomain.waitSampling(5)
    dut.clockDomain.deassertReset()
    dut.clockDomain.waitSampling(20)

    dut.clockDomain.waitSampling(100)
  }
}
