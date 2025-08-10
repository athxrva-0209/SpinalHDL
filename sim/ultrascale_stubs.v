// UltraScale simulation stubs for Verilator

// Differential output buffer
module OBUFDS(
  input  wire I,
  output wire O,
  output wire OB
);
  assign O  = I;
  assign OB = ~I;
endmodule

// Differential bidirectional IO buffer with tristate
module IOBUFDS(
  input  wire I,
  input  wire T,
  output wire O,
  inout  wire IO,
  inout  wire IOB
);
  assign IO  = T ? 1'bz : I;
  assign IOB = T ? 1'bz : ~I;
  assign O   = IO;
endmodule

// Single-ended bidirectional IO buffer with tristate
module IOBUF(
  input  wire I,
  input  wire T,
  output wire O,
  inout  wire IO
);
  assign IO = T ? 1'bz : I;
  assign O  = IO;
endmodule

// Output SERDES (behavioral stub)
module OSERDESE3 #(
  parameter integer DATA_WIDTH = 8,
  parameter INIT = 1'b0,
  parameter IS_CLK_INVERTED = 1'b0,
  parameter IS_CLKDIV_INVERTED = 1'b0,
  parameter IS_RST_INVERTED = 1'b0,
  parameter string ODDR_MODE = "FALSE",
  parameter string SIM_DEVICE = "ULTRASCALE"
)(
  input  wire CLK,
  input  wire CLKDIV,
  input  wire RST,
  input  wire T,
  input  wire [7:0] D,
  output wire OQ,
  output wire T_OUT
);
  assign OQ = D[0];
  assign T_OUT = T;
endmodule

// Input SERDES (minimal stub)
module ISERDESE3 #(
  parameter integer DATA_WIDTH = 8,
  parameter string FIFO_ENABLE = "FALSE",
  parameter string FIFO_SYNC_MODE = "FALSE",
  parameter string IDDR_MODE = "FALSE",
  parameter IS_CLK_INVERTED = 1'b0,
  parameter IS_CLK_B_INVERTED = 1'b0,
  parameter IS_RST_INVERTED = 1'b0,
  parameter string SIM_DEVICE = "ULTRASCALE"
)(
  input  wire CLK,
  input  wire CLK_B,
  input  wire CLKDIV,
  input  wire RST,
  input  wire D,
  input  wire FIFO_RD_CLK,
  input  wire FIFO_RD_EN,
  output wire [7:0] Q,
  output wire FIFO_EMPTY,
  output wire INTERNAL_DIVCLK
);
  assign Q = 8'h00;
  assign FIFO_EMPTY = 1'b1;
  assign INTERNAL_DIVCLK = CLKDIV;
endmodule

// IDELAYCTRL stub
module IDELAYCTRL(
  input  wire REFCLK,
  input  wire RST,
  output wire RDY
);
  assign RDY = 1'b1;
endmodule

// MMCME3_ADV (clock manager) behavioral stub
module MMCME3_ADV #(
  parameter string BANDWIDTH = "OPTIMIZED",
  parameter real   CLKFBOUT_MULT_F = 5.0,
  parameter real   CLKFBOUT_PHASE = 0.0,
  parameter real   CLKOUT0_DIVIDE_F = 5.0,
  parameter string COMPENSATION = "AUTO",
  parameter integer DIVCLK_DIVIDE = 1,
  parameter string STARTUP_WAIT = "FALSE",
  parameter real   CLKIN1_PERIOD = 0.0,
  parameter real   CLKIN2_PERIOD = 0.0,
  parameter real   CLKOUT0_DUTY_CYCLE = 0.5,
  parameter real   CLKOUT1_DUTY_CYCLE = 0.5,
  parameter real   CLKOUT2_DUTY_CYCLE = 0.5,
  parameter real   CLKOUT3_DUTY_CYCLE = 0.5,
  parameter real   CLKOUT4_DUTY_CYCLE = 0.5,
  parameter real   CLKOUT5_DUTY_CYCLE = 0.5,
  parameter real   CLKOUT6_DUTY_CYCLE = 0.5,
  parameter real   CLKOUT0_PHASE = 0.0,
  parameter real   CLKOUT1_PHASE = 0.0,
  parameter real   CLKOUT2_PHASE = 0.0,
  parameter real   CLKOUT3_PHASE = 0.0,
  parameter real   CLKOUT4_PHASE = 0.0,
  parameter real   CLKOUT5_PHASE = 0.0,
  parameter real   CLKOUT6_PHASE = 0.0,
  parameter integer CLKOUT1_DIVIDE = 1,
  parameter integer CLKOUT2_DIVIDE = 1,
  parameter integer CLKOUT3_DIVIDE = 1,
  parameter string  CLKOUT4_CASCADE = "FALSE",
  parameter integer CLKOUT4_DIVIDE = 1,
  parameter integer CLKOUT5_DIVIDE = 1,
  parameter integer CLKOUT6_DIVIDE = 1,
  parameter string  IS_CLKFBIN_INVERTED = "FALSE",
  parameter string  IS_CLKINSEL_INVERTED = "FALSE",
  parameter string  IS_CLKIN1_INVERTED = "FALSE",
  parameter string  IS_CLKIN2_INVERTED = "FALSE",
  parameter string  IS_PSEN_INVERTED = "FALSE",
  parameter string  IS_PSINCDEC_INVERTED = "FALSE",
  parameter string  IS_PWRDWN_INVERTED = "FALSE",
  parameter string  IS_RST_INVERTED = "FALSE",
  parameter real    REF_JITTER1 = 0.0,
  parameter real    REF_JITTER2 = 0.0,
  parameter string  SS_EN = "FALSE",
  parameter integer SS_MOD_PERIOD = 10000,
  parameter string  SS_MODE = "CENTRE_HIGH",
  parameter string  CLKFBOUT_USE_FINE_PS = "FALSE",
  parameter string  CLKOUT0_USE_FINE_PS = "FALSE",
  parameter string  CLKOUT1_USE_FINE_PS = "FALSE",
  parameter string  CLKOUT2_USE_FINE_PS = "FALSE",
  parameter string  CLKOUT3_USE_FINE_PS = "FALSE",
  parameter string  CLKOUT4_USE_FINE_PS = "FALSE",
  parameter string  CLKOUT5_USE_FINE_PS = "FALSE",
  parameter string  CLKOUT6_USE_FINE_PS = "FALSE"
)(
  input  wire CDDCREQ,
  input  wire CLKFBIN,
  input  wire CLKINSEL,
  input  wire CLKIN1,
  input  wire CLKIN2,
  input  wire [6:0] DADDR,
  input  wire DCLK,
  input  wire DEN,
  input  wire [15:0] DI,
  input  wire DWE,
  input  wire PSCLK,
  input  wire PSEN,
  input  wire PSINCDEC,
  input  wire PWRDWN,
  input  wire RST,
  output wire CDDCDONE,
  output wire CLKFBOUT,
  output wire CLKFBOUTB,
  output wire CLKFBSTOPPED,
  output wire CLKINSTOPPED,
  output wire CLKOUT0,
  output wire CLKOUT0B,
  output wire CLKOUT1,
  output wire CLKOUT1B,
  output wire CLKOUT2,
  output wire CLKOUT2B,
  output wire CLKOUT3,
  output wire CLKOUT3B,
  output wire CLKOUT4,
  output wire CLKOUT5,
  output wire CLKOUT6,
  output wire [15:0] DO,
  output wire DRDY,
  output wire LOCKED,
  output wire PSDONE
);
  assign CLKFBOUT = CLKFBIN;
  assign CLKFBOUTB = ~CLKFBOUT;
  assign CLKOUT0 = CLKIN1;
  assign CLKOUT0B = ~CLKOUT0;
  assign CLKOUT1 = CLKIN1;
  assign CLKOUT1B = ~CLKOUT1;
  assign CLKOUT2 = CLKIN1;
  assign CLKOUT2B = ~CLKOUT2;
  assign CLKOUT3 = 1'b0;
  assign CLKOUT3B = 1'b1;
  assign CLKOUT4 = 1'b0;
  assign CLKOUT5 = 1'b0;
  assign CLKOUT6 = 1'b0;
  assign CDDCDONE = 1'b0;
  assign CLKFBSTOPPED = 1'b0;
  assign CLKINSTOPPED = 1'b0;
  assign DO = 16'h0000;
  assign DRDY = 1'b0;
  assign LOCKED = 1'b1;
  assign PSDONE = 1'b0;
endmodule