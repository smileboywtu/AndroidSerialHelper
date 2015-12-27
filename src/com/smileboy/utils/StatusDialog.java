package com.smileboy.utils;

import com.example.lthelper.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class StatusDialog extends DialogFragment {
	
	private UartConfigure mConfigure;
	
	public StatusDialog(UartConfigure config) {
		mConfigure = config;
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new Builder(getActivity());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_status, null);
		
		final TextView status = (TextView) view.findViewById(R.id.state_tv);
		final TextView rate = (TextView) view.findViewById(R.id.rate_status_tv);
		final TextView databits = (TextView) view.findViewById(R.id.data_status_tv);
		final TextView stopbits = (TextView) view.findViewById(R.id.stop_status_tv);
		final TextView parity = (TextView) view.findViewById(R.id.parity_status_tv);
		final TextView flowcontrol = (TextView) view.findViewById(R.id.flow_status_tv);
		
		status.setText(mConfigure.uart_state_str);
		rate.setText(mConfigure.baud_rate_str);
		databits.setText(mConfigure.data_bits_str);
		stopbits.setText(mConfigure.stop_bits_str);
		parity.setText(mConfigure.parity_str);
		flowcontrol.setText(mConfigure.flow_control_str);
		
		builder.setView(view);
		builder.setTitle("Serial Status");
		builder.setPositiveButton("OK", null);
		
		return builder.create();
	}
}
