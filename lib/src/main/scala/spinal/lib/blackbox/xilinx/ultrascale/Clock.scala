package spinal.lib.blackbox.xilinx.ultrascale
import spinal.core._
import spinal.lib._

case class MMCME3_ADV(
                       val BANDWIDTH: String = "OPTIMIZED",
                       val CLKFBOUT_MULT_F: Double = 5.0,
                       val CLKFBOUT_PHASE: Double = 0.0,
                       val CLKOUT0_DIVIDE_F: Double,
                       val COMPENSATION: String = "AUTO",
                       val DIVCLK_DIVIDE: Int = 1,
                       val STARTUP_WAIT: String = "FALSE",
                       val CLKIN1_PERIOD: Double = 0.0,
                       val CLKIN2_PERIOD: Double = 0.0,
                       val CLKOUT0_DUTY_CYCLE: Double = 0.5,
                       val CLKOUT1_DUTY_CYCLE: Double = 0.5,
                       val CLKOUT2_DUTY_CYCLE: Double = 0.5,
                       val CLKOUT3_DUTY_CYCLE: Double = 0.5,
                       val CLKOUT4_DUTY_CYCLE: Double = 0.5,
                       val CLKOUT5_DUTY_CYCLE: Double = 0.5,
                       val CLKOUT6_DUTY_CYCLE: Double = 0.5,
                       val CLKOUT0_PHASE: Double = 0.0,
                       val CLKOUT1_PHASE: Double = 0.0,
                       val CLKOUT2_PHASE: Double = 0.0,
                       val CLKOUT3_PHASE: Double = 0.0,
                       val CLKOUT4_PHASE: Double = 0.0,
                       val CLKOUT5_PHASE: Double = 0.0,
                       val CLKOUT6_PHASE: Double = 0.0,
                       val CLKOUT1_DIVIDE: Int = 1,
                       val CLKOUT2_DIVIDE: Int = 1,
                       val CLKOUT3_DIVIDE: Int = 1,
                       val CLKOUT4_CASCADE: String = "FALSE",
                       val CLKOUT4_DIVIDE: Int = 1,
                       val CLKOUT5_DIVIDE: Int = 1,
                       val CLKOUT6_DIVIDE: Int = 1,
                       val IS_CLKFBIN_INVERTED: Boolean = false,
                       val IS_CLKINSEL_INVERTED: Boolean = false,
                       val IS_CLKIN1_INVERTED: Boolean = false,
                       val IS_CLKIN2_INVERTED: Boolean = false,
                       val IS_PSEN_INVERTED: Boolean = false,
                       val IS_PSINCDEC_INVERTED: Boolean = false,
                       val IS_PWRDWN_INVERTED: Boolean = false,
                       val IS_RST_INVERTED: Boolean = false,
                       val REF_JITTER1: Double = 0.0,
                       val REF_JITTER2: Double = 0.0,
                       val SS_EN: String = "FALSE",
                       val SS_MOD_PERIOD: Int = 10000,
                       val SS_MODE: String = "CENTRE_HIGH",
                       val CLKFBOUT_USE_FINE_PS: String = "FALSE",
                       val CLKOUT0_USE_FINE_PS: String = "FALSE",
                       val CLKOUT1_USE_FINE_PS: String = "FALSE",
                       val CLKOUT2_USE_FINE_PS: String = "FALSE",
                       val CLKOUT3_USE_FINE_PS: String = "FALSE",
                       val CLKOUT4_USE_FINE_PS: String = "FALSE",
                       val CLKOUT5_USE_FINE_PS: String = "FALSE",
                       val CLKOUT6_USE_FINE_PS: String = "FALSE"
                     ) extends BlackBox {
  assert(List("OPTIMIZED", "HIGH", "LOW").contains(BANDWIDTH), message = "Invalid BANDWIDTH")
  assert(CLKFBOUT_MULT_F >= 2.0 && CLKFBOUT_MULT_F <= 64.0, message = "Invalid CLKFBOUT_MULT_F")
  assert(CLKFBOUT_PHASE >= -360.0 && CLKFBOUT_PHASE <= 360.0)
  assert(CLKOUT0_DIVIDE_F >= 1.0 && CLKOUT0_DIVIDE_F <= 128.0)
  assert(DIVCLK_DIVIDE >= 1 && DIVCLK_DIVIDE <= 106)
  assert(CLKIN1_PERIOD >= 0.0 && CLKIN1_PERIOD <= 100.0)
  assert(CLKIN2_PERIOD >= 0.0 && CLKIN2_PERIOD <= 100.0)
  assert(CLKOUT0_DUTY_CYCLE >= 0.001 && CLKOUT0_DUTY_CYCLE <= 0.999)
  assert(CLKOUT1_DUTY_CYCLE >= 0.001 && CLKOUT1_DUTY_CYCLE <= 0.999)
  assert(CLKOUT2_DUTY_CYCLE >= 0.001 && CLKOUT2_DUTY_CYCLE <= 0.999)
  assert(CLKOUT3_DUTY_CYCLE >= 0.001 && CLKOUT3_DUTY_CYCLE <= 0.999)
  assert(CLKOUT4_DUTY_CYCLE >= 0.001 && CLKOUT4_DUTY_CYCLE <= 0.999)
  assert(CLKOUT5_DUTY_CYCLE >= 0.001 && CLKOUT5_DUTY_CYCLE <= 0.999)
  assert(CLKOUT6_DUTY_CYCLE >= 0.001 && CLKOUT6_DUTY_CYCLE <= 0.999)
  assert(CLKOUT0_PHASE >= -360.0 && CLKOUT0_PHASE <= 360.0)
  assert(CLKOUT1_PHASE >= -360.0 && CLKOUT1_PHASE <= 360.0)
  assert(CLKOUT2_PHASE >= -360.0 && CLKOUT2_PHASE <= 360.0)
  assert(CLKOUT3_PHASE >= -360.0 && CLKOUT3_PHASE <= 360.0)
  assert(CLKOUT4_PHASE >= -360.0 && CLKOUT4_PHASE <= 360.0)
  assert(CLKOUT5_PHASE >= -360.0 && CLKOUT5_PHASE <= 360.0)
  assert(CLKOUT6_PHASE >= -360.0 && CLKOUT6_PHASE <= 360.0)
  assert(REF_JITTER1 >= 0.0 && REF_JITTER1 <= 0.999)
  assert(REF_JITTER2 >= 0.0 && REF_JITTER2 <= 0.999)

  val CDDCREQ     = in Bool()
  val CLKFBIN     = in Bool()
  val CLKINSEL    = in Bool()
  val CLKIN1      = in Bool()
  val CLKIN2      = in Bool()
  val DADDR       = in Bits(7 bits)
  val DCLK        = in Bool()
  val DEN         = in Bool()
  val DI          = in Bits(16 bits)
  val DWE         = in Bool()
  val PSCLK       = in Bool()
  val PSEN        = in Bool()
  val PSINCDEC    = in Bool()
  val PWRDWN      = in Bool()
  val RST         = in Bool()

  val CDDCDONE    = out Bool()
  val CLKFBOUT    = out Bool()
  val CLKFBOUTB   = out Bool()
  val CLKFBSTOPPED = out Bool()
  val CLKINSTOPPED = out Bool()
  val CLKOUT0     = out Bool()
  val CLKOUT0B    = out Bool()
  val CLKOUT1     = out Bool()
  val CLKOUT1B    = out Bool()
  val CLKOUT2     = out Bool()
  val CLKOUT2B    = out Bool()
  val CLKOUT3     = out Bool()
  val CLKOUT3B    = out Bool()
  val CLKOUT4     = out Bool()
  val CLKOUT5     = out Bool()
  val CLKOUT6     = out Bool()
  val DO          = out Bits(16 bits)
  val DRDY        = out Bool()
  val LOCKED      = out Bool()
  val PSDONE      = out Bool()



  addGeneric("BANDWIDTH", BANDWIDTH)
  addGeneric("CLKFBOUT_MULT_F", CLKFBOUT_MULT_F)
  addGeneric("CLKFBOUT_PHASE", CLKFBOUT_PHASE)
  addGeneric("CLKOUT0_DIVIDE_F", CLKOUT0_DIVIDE_F)
  addGeneric("COMPENSATION", COMPENSATION)
  addGeneric("DIVCLK_DIVIDE", DIVCLK_DIVIDE)
  addGeneric("STARTUP_WAIT", STARTUP_WAIT)
  addGeneric("CLKIN1_PERIOD", CLKIN1_PERIOD)
  addGeneric("CLKIN2_PERIOD", CLKIN2_PERIOD)

  addGeneric("CLKOUT0_DUTY_CYCLE", CLKOUT0_DUTY_CYCLE)
  addGeneric("CLKOUT1_DUTY_CYCLE", CLKOUT1_DUTY_CYCLE)
  addGeneric("CLKOUT2_DUTY_CYCLE", CLKOUT2_DUTY_CYCLE)
  addGeneric("CLKOUT3_DUTY_CYCLE", CLKOUT3_DUTY_CYCLE)
  addGeneric("CLKOUT4_DUTY_CYCLE", CLKOUT4_DUTY_CYCLE)
  addGeneric("CLKOUT5_DUTY_CYCLE", CLKOUT5_DUTY_CYCLE)
  addGeneric("CLKOUT6_DUTY_CYCLE", CLKOUT6_DUTY_CYCLE)

  addGeneric("CLKOUT0_PHASE", CLKOUT0_PHASE)
  addGeneric("CLKOUT1_PHASE", CLKOUT1_PHASE)
  addGeneric("CLKOUT2_PHASE", CLKOUT2_PHASE)
  addGeneric("CLKOUT3_PHASE", CLKOUT3_PHASE)
  addGeneric("CLKOUT4_PHASE", CLKOUT4_PHASE)
  addGeneric("CLKOUT5_PHASE", CLKOUT5_PHASE)
  addGeneric("CLKOUT6_PHASE", CLKOUT6_PHASE)

  addGeneric("CLKOUT1_DIVIDE", CLKOUT1_DIVIDE)
  addGeneric("CLKOUT2_DIVIDE", CLKOUT2_DIVIDE)
  addGeneric("CLKOUT3_DIVIDE", CLKOUT3_DIVIDE)
  addGeneric("CLKOUT4_CASCADE", CLKOUT4_CASCADE)
  addGeneric("CLKOUT4_DIVIDE", CLKOUT4_DIVIDE)
  addGeneric("CLKOUT5_DIVIDE", CLKOUT5_DIVIDE)
  addGeneric("CLKOUT6_DIVIDE", CLKOUT6_DIVIDE)

  // Boolean attributes (convert to "TRUE"/"FALSE" strings)
  addGeneric("IS_CLKFBIN_INVERTED", if(IS_CLKFBIN_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_CLKINSEL_INVERTED", if(IS_CLKINSEL_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_CLKIN1_INVERTED", if(IS_CLKIN1_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_CLKIN2_INVERTED", if(IS_CLKIN2_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_PSEN_INVERTED", if(IS_PSEN_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_PSINCDEC_INVERTED", if(IS_PSINCDEC_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_PWRDWN_INVERTED", if(IS_PWRDWN_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_RST_INVERTED", if(IS_RST_INVERTED) "TRUE" else "FALSE")

  addGeneric("REF_JITTER1", REF_JITTER1)
  addGeneric("REF_JITTER2", REF_JITTER2)

  addGeneric("SS_EN", SS_EN)
  addGeneric("SS_MOD_PERIOD", SS_MOD_PERIOD)
  addGeneric("SS_MODE", SS_MODE)

  addGeneric("CLKFBOUT_USE_FINE_PS", CLKFBOUT_USE_FINE_PS)
  addGeneric("CLKOUT0_USE_FINE_PS", CLKOUT0_USE_FINE_PS)
  addGeneric("CLKOUT1_USE_FINE_PS", CLKOUT1_USE_FINE_PS)
  addGeneric("CLKOUT2_USE_FINE_PS", CLKOUT2_USE_FINE_PS)
  addGeneric("CLKOUT3_USE_FINE_PS", CLKOUT3_USE_FINE_PS)
  addGeneric("CLKOUT4_USE_FINE_PS", CLKOUT4_USE_FINE_PS)
  addGeneric("CLKOUT5_USE_FINE_PS", CLKOUT5_USE_FINE_PS)
  addGeneric("CLKOUT6_USE_FINE_PS", CLKOUT6_USE_FINE_PS)

  setBlackBoxName("MMCME3_ADV")

//  mapCurrentClockDomain(
//    clock = io.CLKIN1,
//    reset = io.RST
//  )
}

case class PLLE3_ADV(
                      val CLKFBOUT_MULT: Int = 5,
                      val CLKFBOUT_PHASE: Double = 0.0,
                      val CLKIN_PERIOD: Double = 0.0,
                      val CLKOUTPHY_MODE: String = "VCO_2X",
                      val COMPENSATION: String = "AUTO",
                      val DIVCLK_DIVIDE: Int = 1,
                      val REF_JITTER: Double = 0.0,
                      val STARTUP_WAIT: String = "FALSE",
                      val CLKOUT0_DIVIDE: Int = 1,
                      val CLKOUT0_DUTY_CYCLE: Double = 0.5,
                      val CLKOUT0_PHASE: Double = 0.0,
                      val CLKOUT1_DIVIDE: Int = 1,
                      val CLKOUT1_DUTY_CYCLE: Double = 0.5,
                      val CLKOUT1_PHASE: Double = 0.0,
                      val IS_CLKFBIN_INVERTED: Boolean = false,
                      val IS_CLKIN_INVERTED: Boolean = false,
                      val IS_PWRDWN_INVERTED: Boolean = false,
                      val IS_RST_INVERTED: Boolean = false
                    ) extends BlackBox {

  val CLKFBIN     = in Bool()
  val CLKIN       = in Bool()
  val CLKOUTPHYEN = in Bool()
  val DADDR       = in Bits (7 bits)
  val DCLK        = in Bool()
  val DEN         = in Bool()
  val DI          = in Bits(16 bits)
  val DWE         = in Bool()
  val PWRDWN      = in Bool()
  val RST         = in Bool()

  val CLKFBOUT    = out Bool()
  val CLKOUTPHY   = out Bool()
  val CLKOUT0     = out Bool()
  val CLKOUT0B    = out Bool()
  val CLKOUT1     = out Bool()
  val CLKOUT1B    = out Bool()
  val DO          = out Bits(16 bits)
  val DRDY        = out Bool()
  val LOCKED      = out Bool()


  addGeneric("CLKFBOUT_MULT", CLKFBOUT_MULT)
  addGeneric("CLKFBOUT_PHASE", CLKFBOUT_PHASE)
  addGeneric("CLKIN_PERIOD", CLKIN_PERIOD)
  addGeneric("CLKOUTPHY_MODE", CLKOUTPHY_MODE)
  addGeneric("COMPENSATION", COMPENSATION)
  addGeneric("DIVCLK_DIVIDE", DIVCLK_DIVIDE)
  addGeneric("REF_JITTER", REF_JITTER)
  addGeneric("STARTUP_WAIT", STARTUP_WAIT)
  addGeneric("CLKOUT0_DIVIDE", CLKOUT0_DIVIDE)
  addGeneric("CLKOUT0_DUTY_CYCLE", CLKOUT0_DUTY_CYCLE)
  addGeneric("CLKOUT0_PHASE", CLKOUT0_PHASE)
  addGeneric("CLKOUT1_DIVIDE", CLKOUT1_DIVIDE)
  addGeneric("CLKOUT1_DUTY_CYCLE", CLKOUT1_DUTY_CYCLE)
  addGeneric("CLKOUT1_PHASE", CLKOUT1_PHASE)
  addGeneric("IS_CLKFBIN_INVERTED", if(IS_CLKFBIN_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_CLKIN_INVERTED", if(IS_CLKIN_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_PWRDWN_INVERTED", if(IS_PWRDWN_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_RST_INVERTED", if(IS_RST_INVERTED) "TRUE" else "FALSE")

  setBlackBoxName("PLLE3_ADV")

//  mapCurrentClockDomain(
//    clock = io.CLKIN,
//    reset = io.RST
//  )
}

case class BUFG_GT() extends BlackBox {
    val CE = in Bool()
    val CEMASK = in Bool()
    val CLR = in Bool()
    val CLRMASK = in Bool()
    val DIV = in Bits(3 bits)
    val I = in Bool()
    val O = out Bool()
  setBlackBoxName("BUFG_GT")
}


class ClockGenTop extends Component {
  val io = new Bundle {
    val clkIn = in Bool()
    val rst = in Bool()
    val clkOut = out Bool()
    val locked = out Bool()
  }

  // Instantiate the MMCME3_ADV
  val mmcm = MMCME3_ADV(
    CLKFBOUT_MULT_F = 10.0,
    CLKFBOUT_PHASE = 0.0,
    CLKOUT0_DIVIDE_F = 5.0,
    CLKIN1_PERIOD = 10.0 // 100 MHz input
  )

  // Feedback loop
  mmcm.CLKFBIN := mmcm.CLKFBOUT

  // Tie off unused inputs
  mmcm.CLKIN2 := False
  mmcm.CLKINSEL := True
  mmcm.CDDCREQ := False
  mmcm.DADDR := B(0, 7 bits)
  mmcm.DCLK := False
  mmcm.DEN := False
  mmcm.DI := B(0, 16 bits)
  mmcm.DWE := False
  mmcm.PSCLK := False
  mmcm.PSEN := False
  mmcm.PSINCDEC := False
  mmcm.PWRDWN := False
  mmcm.RST := io.rst

  mmcm.CLKIN1 := io.clkIn

  // Instantiate BUFG_GT to buffer the clock
  val buf = BUFG_GT()
  buf.CE := True
  buf.CEMASK := False
  buf.CLR := False
  buf.CLRMASK := False
  buf.DIV := B"000"
  buf.I := mmcm.CLKOUT0

  io.clkOut := buf.O
  io.locked := mmcm.LOCKED
}

object UltraScale_Clock_TopVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new ClockGenTop)
  }
}