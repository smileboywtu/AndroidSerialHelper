package com.smileboy.utils;

import tw.com.prolific.driver.pl2303.PL2303Driver;

public class UartConfigure {

	public String uart_state_str;
	public String baud_rate_str;
	public String data_bits_str;
	public String parity_str;
	public String stop_bits_str;
	public String flow_control_str;

	public UartConfigure() {

		uart_state_str = "No Device";
		baud_rate_str = "115200";
		data_bits_str = "8";
		parity_str = "NONE";
		stop_bits_str = "1";
		flow_control_str = "NONE";
	}

	public UartConfigure(String uart_state, String baud_rate, String data_bits, String parity, String stop_bits,
			String flow_control) {
		super();
		this.uart_state_str = uart_state;
		this.baud_rate_str = baud_rate;
		this.data_bits_str = data_bits;
		this.parity_str = parity;
		this.stop_bits_str = stop_bits;
		this.flow_control_str = flow_control;
	}

	/**
	 * 
	 * @return baud rate parameter
	 */
	public PL2303Driver.BaudRate getBaudRate() {

		if ("57600".equals(baud_rate_str)) {
			return PL2303Driver.BaudRate.B57600;
		} else if ("38400".equals(baud_rate_str)) {
			return PL2303Driver.BaudRate.B38400;
		} else if ("19200".equals(baud_rate_str)) {
			return PL2303Driver.BaudRate.B19200;
		} else if ("9600".equals(baud_rate_str)) {
			return PL2303Driver.BaudRate.B9600;
		}

		return PL2303Driver.BaudRate.B115200;
	}

	/**
	 * 
	 * @return data bits parameter
	 */
	public PL2303Driver.DataBits getDataBits() {

		if ("7".equals(data_bits_str)) {
			return PL2303Driver.DataBits.D7;
		}

		return PL2303Driver.DataBits.D8;
	}

	/**
	 * 
	 * @return stop bits parameter
	 */
	public PL2303Driver.StopBits getStopBits() {

		if ("2".equals(stop_bits_str)) {
			return PL2303Driver.StopBits.S2;
		}

		return PL2303Driver.StopBits.S1;
	}

	/**
	 * 
	 * @return parity parameter
	 */
	public PL2303Driver.Parity getpaParity() {

		if ("ODD".equals(parity_str)) {
			return PL2303Driver.Parity.ODD;
		} else if ("EVEN".equals(parity_str)) {
			return PL2303Driver.Parity.EVEN;
		}

		return PL2303Driver.Parity.NONE;
	}

	/**
	 * 
	 * @return flow control parameter
	 */
	public PL2303Driver.FlowControl getFlowControl() {

		if ("CTS/RTS".equals(flow_control_str)) {
			return PL2303Driver.FlowControl.RTSCTSDTRDSR;
		} else if ("DTR/DSR".equals(flow_control_str)) {
			return PL2303Driver.FlowControl.DTRDSR;
		} else if ("XOFF/XON".equals(flow_control_str)) {
			return PL2303Driver.FlowControl.XONXOFF;
		}

		return PL2303Driver.FlowControl.OFF;
	}
}
