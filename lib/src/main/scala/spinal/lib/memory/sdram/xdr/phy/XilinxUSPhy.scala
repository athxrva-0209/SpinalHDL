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


