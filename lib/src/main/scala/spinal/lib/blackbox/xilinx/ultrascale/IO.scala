package spinal.lib.blackbox.xilinx.ultrascale
import spinal.core._
import spinal.lib._

case class IBUFDS() extends BlackBox {
    val I  = in Bool()
    val IB = in Bool()
    val O  = out Bool()
  setBlackBoxName("IBUFDS")
}

case class OBUFDS() extends BlackBox {
    val I  = in Bool()
    val O  = out Bool()
    val OB = out Bool()

  setBlackBoxName("OBUFDS")
}

case class IOBUFDS() extends BlackBox {
    val I   = in Bool()
    val T   = in Bool()
    val O   = out Bool()
    val IO  = inout(Analog(Bool()))
    val IOB = inout(Analog(Bool()))
  setBlackBoxName("IOBUFDS")
}

case class ISERDESE3(
                      val DATA_WIDTH: Int = 8,
                      val FIFO_ENABLE: String = "FALSE",
                      val FIFO_SYNC_MODE: String = "FALSE",
                      val IDDR_MODE: String = "FALSE",
                      val IS_CLK_INVERTED: Boolean = false,
                      val IS_CLK_B_INVERTED: Boolean = false,
                      val IS_RST_INVERTED: Boolean = false,
                      val SIM_DEVICE: String = "ULTRASCALE"  // Do not change

                    ) extends BlackBox {
  require(DATA_WIDTH == 4 || DATA_WIDTH == 8, "ISERDESE3 DATA_WIDTH must be 4, or 8.")
  require(FIFO_ENABLE == "TRUE" || FIFO_ENABLE == "FALSE", "FIFO_ENABLE must be 'TRUE' or 'FALSE'.")


    val CLK     = in Bool()
    val CLK_B   = in Bool()
    val CLKDIV  = in Bool()
    val RST     = in Bool()
    val D       = in Bool()
    val FIFO_RD_CLK = in Bool()
    val FIFO_RD_EN = in Bool()
    val Q = out Bits(8 bits)
    val FIFO_EMPTY = out Bool()
    val INTERNAL_DIVCLK = out Bool()


  addGeneric("DATA_WIDTH", DATA_WIDTH)
  addGeneric("FIFO_ENABLE", FIFO_ENABLE)
  addGeneric("FIFO_SYNC_MODE", FIFO_SYNC_MODE)
  addGeneric("IDDR_MODE", IDDR_MODE)
  addGeneric("IS_CLK_INVERTED", if(IS_CLK_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_CLK_B_INVERTED", if(IS_CLK_B_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_RST_INVERTED", if(IS_RST_INVERTED) "TRUE" else "FALSE")
  addGeneric("SIM_DEVICE", SIM_DEVICE)

  setBlackBoxName("ISERDESE3")

//  mapCurrentClockDomain(
//    clock = io.CLKDIV,
//    reset = io.RST
//  )
}

case class OSERDESE3(
                      val DATA_WIDTH: Int = 8,
                      val INIT: Boolean = false,
                      val IS_CLK_INVERTED: Boolean = false,
                      val IS_CLKDIV_INVERTED: Boolean = false,
                      val IS_RST_INVERTED: Boolean = false,
                      val ODDR_MODE: String = "FALSE",
                      val SIM_DEVICE: String = "ULTRASCALE"
                    ) extends BlackBox {
  require(DATA_WIDTH == 4 || DATA_WIDTH == 8, "OSERDESE3 DATA_WIDTH must be 4, or 8.")
  require(INIT == false || INIT == true, "INIT must be true or false.")

    val CLK     = in Bool()
    val CLKDIV  = in Bool()
    val RST     = in Bool()
    val T       = in Bool()
    val D      = in Bits(8 bits)
    val OQ      = out Bool()
    val T_OUT   = out Bool()


  addGeneric("DATA_WIDTH", DATA_WIDTH)
  addGeneric("INIT", if (INIT) "1'b1" else "1'b0")
  addGeneric("IS_CLK_INVERTED", if(IS_CLK_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_CLKDIV_INVERTED", if(IS_CLKDIV_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_RST_INVERTED", if(IS_RST_INVERTED) "TRUE" else "FALSE")
  addGeneric("ODDR_MODE", ODDR_MODE)
  addGeneric("SIM_DEVICE", SIM_DEVICE)

  setBlackBoxName("OSERDESE3")

//  mapCurrentClockDomain(
//    clock = io.CLKDIV,
//    reset = io.RST
//  )
}

case class IDELAYE3(
                     val CASCADE: String = "NONE",
                     val DELAY_FORMAT: String = "TIME",
                     val DELAY_SRC: String = "IDATAIN",
                     val DELAY_TYPE: String = "FIXED",
                     val DELAY_VALUE: Int = 0,
                     val IS_CLK_INVERTED: Boolean = false,
                     val IS_RST_INVERTED: Boolean = false,
                     val REFCLK_FREQUENCY: Int = 300,
                     val SIM_DEVICE: String = "ULTRASCALE"
                   ) extends BlackBox {
  require(CASCADE == "NONE" || CASCADE == "MASTER" || CASCADE == "SLAVE_END" || CASCADE == "SLAVE_MIDDLE")
  require(DELAY_FORMAT == "COUNT" || DELAY_FORMAT == "TIME")
  require(DELAY_TYPE == "FIXED" || DELAY_TYPE == "VARIABLE" || DELAY_TYPE == "VAR_LOAD")
  require(DELAY_VALUE >= 0 && DELAY_VALUE <= 1250)


    val CASC_IN         = in Bool()
    val CASC_RETURN     = in Bool()
    val CE              = in Bool()
    val CLK             = in Bool()
    val CNTVALUEIN      = in Bits(9 bits)
    val DATAIN          = in Bool()
    val EN_VTC          = in Bool()
    val IDATAIN         = in Bool()
    val INC             = in Bool()
    val LOAD            = in Bool()
    val RST           = in Bool()

    val CASC_OUT        = out Bool()
    val CNTVALUEOUT     = out Bits(9 bits)
    val DATAOUT         = out Bool()



  addGeneric("CASCADE", CASCADE)
  addGeneric("DELAY_FORMAT", DELAY_FORMAT)
  addGeneric("DELAY_SRC", DELAY_SRC)
  addGeneric("DELAY_TYPE", DELAY_TYPE)
  addGeneric("DELAY_VALUE", DELAY_VALUE)
  addGeneric("IS_CLK_INVERTED", if(IS_CLK_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_RST_INVERTED", if(IS_RST_INVERTED) "TRUE" else "FALSE")
  addGeneric("REFCLK_FREQUENCY", REFCLK_FREQUENCY)
  addGeneric("SIM_DEVICE", SIM_DEVICE)

  setBlackBoxName("IDELAYE3")
}

case class ODELAYE3(
                     val CASCADE: String = "NONE",
                     val DELAY_FORMAT: String = "TIME",
                     val DELAY_TYPE: String = "FIXED",
                     val DELAY_VALUE: Int = 0,
                     val IS_CLK_INVERTED: Boolean = false,
                     val IS_RST_INVERTED: Boolean = false,
                     val REFCLK_FREQUENCY: Int = 300,
                     val SIM_DEVICE: String = "ULTRASCALE",
                     val UPDATE_MODE: String = "ASYNC"
                   ) extends BlackBox {
  require(CASCADE == "NONE" || CASCADE == "MASTER" || CASCADE == "SLAVE_END" || CASCADE == "SLAVE_MIDDLE")
  require(DELAY_FORMAT == "COUNT" || DELAY_FORMAT == "TIME")
  require(DELAY_TYPE == "FIXED" || DELAY_TYPE == "VARIABLE" || DELAY_TYPE == "VAR_LOAD")
  require(DELAY_VALUE >= 0 && DELAY_VALUE <= 1250)

    val CASC_IN         = in Bool()
    val CASC_RETURN     = in Bool()
    val CE              = in Bool()
    val CLK             = in Bool()
    val CNTVALUEIN      = in Bits(9 bits)
    val EN_VTC          = in Bool()
    val INC             = in Bool()
    val LOAD            = in Bool()
    val ODATAIN         = in Bool()
    val RST             = in Bool()

    val CASC_OUT        = out Bool()
    val CNTVALUEOUT     = out Bits(9 bits)
    val DATAOUT         = out Bool()


  addGeneric("CASCADE", CASCADE)
  addGeneric("DELAY_FORMAT", DELAY_FORMAT)
  addGeneric("DELAY_TYPE", DELAY_TYPE)
  addGeneric("DELAY_VALUE", DELAY_VALUE)
  addGeneric("IS_CLK_INVERTED", if(IS_CLK_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_RST_INVERTED", if(IS_RST_INVERTED) "TRUE" else "FALSE")
  addGeneric("REFCLK_FREQUENCY", REFCLK_FREQUENCY)
  addGeneric("SIM_DEVICE", SIM_DEVICE)
  addGeneric("UPDATE_MODE", UPDATE_MODE)

  setBlackBoxName("ODELAYE3")
}

case class IDELAYCTRL() extends BlackBox {

    val REFCLK = in Bool()
    val RST    = in Bool()
    val RDY    = out Bool()


  setBlackBoxName("IDELAYCTRL")
//  mapCurrentClockDomain(
//    clock = io.REFCLK,
//    reset = io.RST
//  )
}

class IOGenTop extends Component {
  val io = new Bundle {
    // Differential pairs
    val ibufds_i   = in Bool()
    val ibufds_ib  = in Bool()
    val ibufds_o   = out Bool()

    val obufds_i   = in Bool()
    val obufds_o   = out Bool()
    val obufds_ob  = out Bool()

    // ISERDES/OSERDES clocks and data
    val clk        = in Bool()
    val clk_b      = in Bool()
    val clkdiv     = in Bool()
    val rst        = in Bool()

    // IOBUFDS tri-state and IO pins
    val iobufds_i  = in Bool()
    val iobufds_t  = in Bool()
    val iobufds_o  = out Bool()
    val iobufds_io = inout(Analog(Bool()))
    val iobufds_iob= inout(Analog(Bool()))

    // IDELAYCTRL
    val refclk     = in Bool()
    val idelayctrl_rdy = out Bool()
  }

  // ------------------------------------------------------------
  // Instantiate BlackBoxes
  // ------------------------------------------------------------

  // 1. IBUFDS
  val ibufds = IBUFDS()
  ibufds.I  := io.ibufds_i
  ibufds.IB := io.ibufds_ib
  io.ibufds_o  := ibufds.O

  // 2. OBUFDS
  val obufds = OBUFDS()
  obufds.I := io.obufds_i
  io.obufds_o := obufds.O
  io.obufds_ob:= obufds.OB

  // 3. IOBUFDS
  val iobufds = IOBUFDS()
  iobufds.I   := io.iobufds_i
  iobufds.T   := io.iobufds_t
  io.iobufds_o   := iobufds.O
  iobufds.IO  <> io.iobufds_io
  iobufds.IOB <> io.iobufds_iob

  // 4. ISERDESE3 (with generics default)
  val iserdes = ISERDESE3()
  iserdes.CLK     := io.clk
  iserdes.CLK_B   := io.clk_b
  iserdes.CLKDIV  := io.clkdiv
  iserdes.RST     := io.rst
  iserdes.D       := False
  iserdes.FIFO_RD_CLK := False
  iserdes.FIFO_RD_EN  := False
  // Q, FIFO_EMPTY, INTERNAL_DIVCLK go unused for now

  // 5. OSERDESE3 (with generics default)
  val oserdes = OSERDESE3()
  oserdes.CLK     := io.clk
  oserdes.CLKDIV  := io.clkdiv
  oserdes.RST     := io.rst
  oserdes.T       := False
  oserdes.D      := B(0, 8 bits)
  // OQ, T_OUT unused for now

  // 6. IDELAYE3
  val idelaye3 = IDELAYE3()
  idelaye3.CASC_IN     := False
  idelaye3.CASC_RETURN := False
  idelaye3.CE          := False
  idelaye3.CLK         := io.clk
  idelaye3.CNTVALUEIN  := B(0, 9 bits)
  idelaye3.DATAIN      := False
  idelaye3.EN_VTC      := False
  idelaye3.IDATAIN     := False
  idelaye3.INC         := False
  idelaye3.LOAD        := False
  idelaye3.RST         := io.rst
  // Outputs ignored for now

  // 7. ODELAYE3
  val odelaye3 = ODELAYE3()
  odelaye3.CASC_IN     := False
  odelaye3.CASC_RETURN := False
  odelaye3.CE          := False
  odelaye3.CLK         := io.clk
  odelaye3.CNTVALUEIN  := B(0, 9 bits)
  odelaye3.EN_VTC      := False
  odelaye3.INC         := False
  odelaye3.LOAD        := False
  odelaye3.ODATAIN     := False
  odelaye3.RST         := io.rst
  // Outputs ignored for now

  // 8. IDELAYCTRL
  val idelayctrl = IDELAYCTRL()
  idelayctrl.REFCLK := io.refclk
  idelayctrl.RST    := io.rst
  io.idelayctrl_rdy    := idelayctrl.RDY
}

object UltraScale_IO_TopVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new IOGenTop)
  }
}
