package spinal.lib.blackbox.xilinx.ultrascale
import spinal.core._
import spinal.lib._

case class FDRE(
                 val INIT: Boolean = false,
                 val IS_C_INVERTED: Boolean = false,
                 val IS_D_INVERTED: Boolean = false,
                 val IS_R_INVERTED: Boolean = false,
               ) extends BlackBox {
  val D = in Bool()
  val CE = in Bool()
  val C = in Bool()
  val R = in Bool()
  val Q = out Bool()

  addGeneric("INIT", if(INIT) "1'b1" else "1'b0")
  addGeneric("IS_C_INVERTED", if(IS_C_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_D_INVERTED", if(IS_D_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_R_INVERTED", if(IS_R_INVERTED) "TRUE" else "FALSE")

  setBlackBoxName("FDRE")

//  mapCurrentClockDomain(
//    clock = io.C,
//    reset = io.R
//  )
}

case class FDSE(
                 val INIT: Boolean = false,
                 val IS_C_INVERTED: Boolean = false,
                 val IS_D_INVERTED: Boolean = false,
                 val IS_S_INVERTED: Boolean = false,
               ) extends BlackBox {
  val D = in Bool()
  val CE = in Bool()
  val C = in Bool()
  val S = in Bool()
  val Q = out Bool()

  addGeneric("INIT", if(INIT) "1'b1" else "1'b0")
  addGeneric("IS_C_INVERTED", if(IS_C_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_D_INVERTED", if(IS_D_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_S_INVERTED", if(IS_S_INVERTED) "TRUE" else "FALSE")

  setBlackBoxName("FDSE")

//  mapCurrentClockDomain(clock = io.C, reset = io.S)
}

case class FDPE(
                 val INIT: Boolean = false,
                 val IS_C_INVERTED: Boolean = false,
                 val IS_D_INVERTED: Boolean = false,
                 val IS_PRE_INVERTED: Boolean = false,
               ) extends BlackBox {
  val D = in Bool()
  val CE = in Bool()
  val C = in Bool()
  val PRE = in Bool()
  val Q = out Bool()

  addGeneric("INIT", if(INIT) "1'b1" else "1'b0")
  addGeneric("IS_C_INVERTED", if(IS_C_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_D_INVERTED", if(IS_D_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_PRE_INVERTED", if(IS_PRE_INVERTED) "TRUE" else "FALSE")

  setBlackBoxName("FDPE")

//  mapCurrentClockDomain(clock = io.C, reset = io.PRE)
}

case class IDDRE1(
                   val DDR_CLK_EDGE: String = "OPPOSITE_EDGE",
                   val IS_C_INVERTED: Boolean = false,
                   val IS_CB_INVERTED: Boolean = false,
                 ) extends BlackBox {
  require(Set("SAME_EDGE", "OPPOSITE_EDGE", "SAME_EDGE_PIPELINED").contains(DDR_CLK_EDGE),
    s"DDR_CLK_EDGE ($DDR_CLK_EDGE) must be 'SAME_EDGE', 'OPPOSITE_EDGE', or 'SAME_EDGE_PIPELINED'.")

  val C = in Bool()
  val CB = in Bool()
  val D = in Bool()
  val R = in Bool()
  val Q1 = out Bool()
  val Q2 = out Bool()

  addGeneric("DDR_CLK_EDGE", DDR_CLK_EDGE)
  addGeneric("IS_C_INVERTED", if(IS_C_INVERTED) "TRUE" else "FALSE")
  addGeneric("IS_CB_INVERTED", if(IS_CB_INVERTED) "TRUE" else "FALSE")

  setBlackBoxName("IDDRE1")

//  mapCurrentClockDomain(
//    clock = io.C,
//    reset = io.R
//  )
}

//case class ODDRE1(
//                   val IS_C_INVERTED: Boolean = false,
//                   val IS_D1_INVERTED: Boolean = false,
//                   val IS_D2_INVERTED: Boolean = false,
//                   val SIM_DEVICE: String = "ULTRASCALE",
//                   val SRVAL: Boolean = false,
//                 ) extends BlackBox {
//
//  val C = in Bool()
//  val D1 = in Bool()
//  val D2 = in Bool()
//  val SR = in Bool()
//  val Q = out Bool()
//
//  addGeneric("IS_C_INVERTED", if(IS_C_INVERTED) "TRUE" else "FALSE")
//  addGeneric("IS_D1_INVERTED", if(IS_D1_INVERTED) "TRUE" else "FALSE")
//  addGeneric("IS_D2_INVERTED", if(IS_D2_INVERTED) "TRUE" else "FALSE")
//  addGeneric("SIM_DEVICE", SIM_DEVICE)
//  addGeneric("SRVAL", SRVAL)
//
//
//  setDefinitionName("ODDRE1")
//  setBlackBoxName("ODDRE1")
//
//  mapCurrentClockDomain(
//    clock = io.C,
//    reset = io.SR
//  )
//}

class RegisterGenTop extends Component {
  val io = new Bundle {
    val clk = in Bool()
    val clk_b = in Bool()
    val rst = in Bool()
    val pre = in Bool()
    val set = in Bool()
//    val sr = in Bool()
    val d = in Bool()
//    val d1 = in Bool()
//    val d2 = in Bool()
    val iddrQ1 = out Bool()
    val iddrQ2 = out Bool()
//    val oddrQ = out Bool()
    val fdreQ = out Bool()
    val fdseQ = out Bool()
    val fdpeQ = out Bool()
  }

  // FDRE (D flip-flop with reset)
  val fdre = FDRE()
  fdre.C := io.clk
  fdre.D := io.d
  fdre.CE := True
  fdre.R := io.rst
  io.fdreQ := fdre.Q

  // FDSE (D flip-flop with set)
  val fdse = FDSE()
  fdse.C := io.clk
  fdse.D := io.d
  fdse.CE := True
  fdse.S := io.set
  io.fdseQ := fdse.Q

  // FDPE (D flip-flop with preset)
  val fdpe = FDPE()
  fdpe.C := io.clk
  fdpe.D := io.d
  fdpe.CE := True
  fdpe.PRE := io.pre
  io.fdpeQ := fdpe.Q

  // IDDRE1 (Input DDR register)
  val iddr = IDDRE1()
  iddr.C := io.clk
  iddr.CB := io.clk_b
  iddr.D := io.d
  iddr.R := io.rst
  io.iddrQ1 := iddr.Q1
  io.iddrQ2 := iddr.Q2

//  // ODDRE1 (Output DDR register)
//  val oddr = ODDRE1()
//  oddr.C := io.clk
//  oddr.D1 := io.d1
//  oddr.D2 := io.d2
//  oddr.SR := io.sr
//  io.oddrQ := oddr.Q
}

object UltraScale_Register_TopVerilog {
  def main(args: Array[String]): Unit = {
    SpinalVerilog(new RegisterGenTop)
  }
}