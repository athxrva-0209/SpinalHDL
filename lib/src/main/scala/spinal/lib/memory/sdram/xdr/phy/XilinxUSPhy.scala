package spinal.lib.memory.sdram.xdr.phy

import spinal.core._
import spinal.lib._
import spinal.lib.blackbox.xilinx.ultrascale._
import spinal.lib.bus.misc.BusSlaveFactory
import spinal.lib.memory.sdram.SdramLayout
import spinal.lib.memory.sdram.xdr.{PhyLayout, SdramXdrIo, SdramXdrPhyCtrl}
import spinal.core.sim._
import spinal.lib.memory.sdram._


object XilinxUSPhy {
  def phyLayout(sl: SdramLayout, clkRatio: Int) = PhyLayout(
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

case class XilinxUSPhy(sl: SdramLayout,
                       clkRatio: Int,
                       clk90: ClockDomain,
                       serdesClk0: ClockDomain,
                       serdesClk90: ClockDomain) extends Component {

  val pl = XilinxUSPhy.phyLayout(sl, clkRatio)
  val io = new Bundle {
    val ctrl  = slave(SdramXdrPhyCtrl(pl))
    val sdram = master(SdramXdrIo(sl))
  }

  assert(clkRatio == 2)
  val phaseCount = clkRatio

  val clk270Rst = ResetCtrl.asyncAssertSyncDeassert(
    input         = ClockDomain.current.isResetActive,
    clockDomain   = clk90.withRevertedClockEdge(),
    inputPolarity = HIGH,
    outputPolarity= HIGH
  )
  val clk270 = clk90.withRevertedClockEdge().copy(reset = clk270Rst)

  val clk90Rst = ResetCtrl.asyncAssertSyncDeassert(
    input         = clk270Rst,
    clockDomain   = clk90,
    inputPolarity = HIGH,
    outputPolarity= HIGH
  )

  val idelayctrl = IDELAYCTRL()
  idelayctrl.REFCLK := serdesClk90.readClockWire
  idelayctrl.RST    := ClockDomain.isResetActive

  def seqToOutput(name: String, seq: Seq[Bool], outputEnable: Seq[Bool], phase90: Boolean = false): (Bool, Bool) = {
    val oser = OSERDESE3(DATA_WIDTH = phaseCount*2)
    val dataBits = Bits(8 bits)
    for (i <- 0 until phaseCount*2) dataBits(i) := seq(i)
    for (i <- phaseCount*2 until 8) dataBits(i) := False
    oser.D := dataBits

    if (!phase90) {
      oser.CLK    := serdesClk0.readClockWire
      oser.CLKDIV := ClockDomain.current.readClockWire
      oser.RST    := ClockDomain.current.isResetActive
    } else {
      oser.CLK    := serdesClk90.readClockWire
      oser.CLKDIV := clk90.readClockWire
      oser.RST    := clk90Rst
    }

    // Single tri-state control
    oser.T := !outputEnable.orR
    (oser.OQ, oser.T_OUT)
  }

  def valueToOutput(name: String, phase: Bool) = seqToOutput(name, Seq.fill(4*2)(phase), Seq.fill(4*2)(True))._1
  def sdrToOutput(name: String, phases: Seq[Bool]) = seqToOutput(name, phases.flatMap(p => Seq.fill(2)(p)), Seq.fill(phaseCount*2)(True))._1
  def ddrToOutput(name: String, phases: Seq[Seq[Bool]]) = seqToOutput(name, phases.flatten, Seq.fill(phaseCount*2)(True))._1

  val clkBuf = OBUFDS()
  clkBuf.I := seqToOutput("CK", Seq.fill(phaseCount)(Seq(True, False)).flatten, Seq.fill(phaseCount*2)(True), phase90 = true)._1
  io.sdram.CK  := clkBuf.O
  io.sdram.CKn := clkBuf.OB

  for (i <- 0 until sl.chipAddressWidth) io.sdram.ADDR(i) := valueToOutput("ADDR", RegNext(io.ctrl.ADDR(i)))
  for (i <- 0 until sl.bankWidth)        io.sdram.BA(i)   := valueToOutput("BA", RegNext(io.ctrl.BA(i)))
  io.sdram.CASn := sdrToOutput("CASn", io.ctrl.phases.map(p => RegNext(p.CASn)))
  io.sdram.CKE  := sdrToOutput("CKE",  io.ctrl.phases.map(p => RegNext(p.CKE)))
  io.sdram.CSn  := sdrToOutput("CSn",  io.ctrl.phases.map(p => RegNext(p.CSn)))
  io.sdram.RASn := sdrToOutput("RASn", io.ctrl.phases.map(p => RegNext(p.RASn)))
  io.sdram.WEn  := sdrToOutput("WEn",  io.ctrl.phases.map(p => RegNext(p.WEn)))
  if (sl.generation.RESETn) io.sdram.RESETn := sdrToOutput("RESETn", io.ctrl.phases.map(p => RegNext(p.RESETn)))
  io.sdram.ODT := sdrToOutput("ODT", io.ctrl.phases.map(p => RegNext(p.ODT)))

  val idelayValueIn = in Bits(9 bits) // UltraScale: 9 bits

  val dqe0Reg = RegNext(io.ctrl.writeEnable) init(False)
  val dqe270Reg = clk270(RegNext(io.ctrl.writeEnable) init(False))
  val dqstReg = clk270(RegNext(Vec((io.ctrl.writeEnable ## dqe270Reg).mux(
    B"00" -> B"0000",
    B"10" -> B"0011",
    B"11" -> B"1111",
    B"01" -> B"1111"
  ).asBools.reverse)))
  dqstReg.foreach(_.init(False))

  val dqReg = io.ctrl.phases.map(p => RegNext(p.DQw))
  val dmReg = io.ctrl.phases.map(p => RegNext(p.DM))

  val dqs = for (i <- 0 until (sl.dataWidth+7)/8) yield new Area {
    val (serQ, serT) = seqToOutput("DQS", Seq.fill(phaseCount)(Seq(True, False)).flatten, dqstReg, phase90 = true)
    val buf = IOBUFDS()
    buf.T := serT
    buf.I := serQ
    io.sdram.DQS(i)  := buf.IO
    io.sdram.DQSn(i) := buf.IOB
  }

  val dm = for (i <- 0 until (sl.dataWidth+7)/8) yield new Area {
    io.sdram.DM(i) := ddrToOutput("DM", dmReg.map(_.map(_(i))))
  }

  io.ctrl.readValid := io.ctrl.readEnable

  val dq = for (i <- 0 until sl.dataWidth) yield new Area {
    val buf = IOBUF()
    io.sdram.DQ(i) := buf.IO

    val (serQ, serT) = seqToOutput("DQ", dqReg.map(_.map(_(i))).flatten, List.fill(4)(dqe0Reg))
    buf.T := serT
    buf.I := serQ

    val idelay = IDELAYE3(DELAY_TYPE = "VAR_LOAD")
    idelay.CLK         := serdesClk0.readClockWire
    idelay.IDATAIN     := buf.O
    idelay.DATAIN      := False
    idelay.CE          := False
    idelay.INC         := True
    idelay.LOAD        := in Bool()
    idelay.CNTVALUEIN  := idelayValueIn
    idelay.RST         := False
    idelay.EN_VTC      := True

    val des = ISERDESE3(DATA_WIDTH = phaseCount*2)
    des.CLK    := serdesClk0.readClockWire
    des.CLK_B  := !serdesClk0.readClockWire
    des.CLKDIV := ClockDomain.current.readClockWire
    des.RST    := ClockDomain.current.isResetActive
    des.D      := idelay.DATAOUT

    for (phase <- 0 until phaseCount; ratio <- 0 until pl.dataRate) {
      io.ctrl.phases(phase).DQr(ratio)(i) := des.Q((phaseCount*pl.dataRate-1) - (phase*pl.dataRate + ratio))
    }
  }

  def driveFrom(mapper: BusSlaveFactory): Unit = {
    mapper.drive(idelayValueIn, 0x00)
    mapper.driveMultiWord(Vec(dq.map(_.idelay.LOAD)), 0x20)
    mapper.driveMultiWord(Vec(dq.map(_ => False)), 0x40)
  }
}



//object USPhyWriteTest extends App {
//
//  class USPhyWriteTop extends Component {
//    val io = new Bundle {
//      val clkIn = in Bool() // Single reference clock (e.g., 100 MHz)
//      val reset = in Bool()
//    }
//
//    // Example DDR3 SDRAM layout
//    val sdramLayout = SdramLayout(
//      generation = SdramGeneration.DDR3,
//      dataWidth = 8,              // 8-bit DQ
//      columnWidth = 14,
//      bankWidth = 3,
//      rowWidth = 8
//    )
//
//    // Clock generator for UltraScale (similar to S7's PLLE2_ADV)
//    val mmcm = MMCME3_ADV(
//      CLKFBOUT_MULT_F = 8.0,
//      CLKIN1_PERIOD = 10.0,     // 100 MHz input
//      CLKOUT0_DIVIDE_F = 4.0,   // Parallel clock
//      CLKOUT1_DIVIDE = 4,       // 90° phase shift
//      CLKOUT1_PHASE = 90.0
//    )
//
//    mmcm.CLKFBIN := mmcm.CLKFBOUT
//    mmcm.CLKIN1 := io.clkIn
//    mmcm.RST := io.reset
//
//    // Create clock domains
//    val serdesClk0 = ClockDomain(mmcm.CLKOUT0, io.reset)
//    val serdesClk90 = ClockDomain(mmcm.CLKOUT1, io.reset)
//
//    // In UltraScale, clk90 for control can be same as serdesClk90
//    val phy = XilinxUSPhy(
//      sl = sdramLayout,
//      clkRatio = 2,
//      clk90 = serdesClk90,
//      serdesClk0 = serdesClk0,
//      serdesClk90 = serdesClk90
//    )
//
//    // Drive a counter pattern into write data
//    val counterArea = new ClockingArea(serdesClk0) {
//      val counter = Reg(UInt(8 bits)) init(0)
//      counter := counter + 1
//
//      phy.io.ctrl.phases(0).WEn := True
//      for (bit <- 0 until 8) {
//        phy.io.ctrl.phases(0).DQw(0)(bit) := counter(bit)
//      }
//    }
//
//    // Tie off unused
//    for (i <- 0 until phy.io.ctrl.ADDR.getWidth)
//      phy.io.ctrl.ADDR(i) := False
//    for (phase <- phy.io.ctrl.phases) {
//      for (dm <- phase.DM) {  // DM is probably a Vec of Bits
//        for (i <- 0 until dm.getWidth)
//          dm(i) := False
//      }
//      phase.WEn := False
//    }
//  }
//
//  // Simulation config
//  SimConfig.withWave.compile(new USPhyWriteTop).doSim { dut =>
//    dut.io.reset #= true
//    dut.io.clkIn #= false
//
//    // Generate single reference clock
//    fork {
//      while (true) {
//        dut.io.clkIn #= false
//        sleep(5)
//        dut.io.clkIn #= true
//        sleep(5)
//      }
//    }
//
//    sleep(20)
//    dut.io.reset #= false
//
//    // Run for some cycles to see DQ/DQS toggling
//    sleep(500)
//  }
//}

