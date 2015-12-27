package com.smileboy.utils;

import com.example.lthelper.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import tw.com.prolific.driver.pl2303.PL2303Driver;

public class GeneralSerialUtil extends DialogFragment {

	private static final int READ_THREAD_STOP = 0;

	private PL2303Driver mSerial;

	private Button mSendButton;
	private Button mClearButton;
	private EditText mSendPane;
	private EditText mReadPane;
	private TextView mSendBytes;
	private TextView mReadBytes;

	private long mSendCounter = 0;
	private long mReadCounter = 0;

	private byte[] buffer = new byte[1024];

	private Handler mHandler = new Handler();

	public GeneralSerialUtil(PL2303Driver serial) {
		mSerial = serial;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_serial, null);

		mSendButton = (Button) view.findViewById(R.id.send_btn);
		mClearButton = (Button) view.findViewById(R.id.clear_btn);
		mSendPane = (EditText) view.findViewById(R.id.send_tv);
		mReadPane = (EditText) view.findViewById(R.id.receive_tv);
		mSendBytes = (TextView) view.findViewById(R.id.sendcounter_tv);
		mReadBytes = (TextView) view.findViewById(R.id.readcounter_tv);

		mSendButton.setOnClickListener(sendCallback);
		mClearButton.setOnClickListener(clearCallback);

		mHandler.post(mReadThread);

		builder.setView(view);
		builder.setTitle("PL2303 Serial");

		return builder.create();
	}

	@Override
	public void onDestroyView() {
		// here should stop the dialog
		mHandler.sendEmptyMessage(READ_THREAD_STOP);
		super.onDestroyView();
	}

	private Button.OnClickListener sendCallback = new OnClickListener() {

		@Override
		public void onClick(View v) {
			byte[] toSend = mSendPane.getText().toString().getBytes();
			int len = mSerial.write(toSend);
			mSendCounter += len;
			// update label
			mSendBytes.setText("" + mSendCounter);
		}
	};

	private Button.OnClickListener clearCallback = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mSendCounter = 0;
			mReadCounter = 0;
			mSendPane.setText("");
			mReadPane.setText("");
			mSendBytes.setText("0");
			mReadBytes.setText("0");
		}
	};

	private Runnable mReadThread = new Runnable() {

		@Override
		public void run() {

			if (mHandler.hasMessages(READ_THREAD_STOP)) {
				return;
			}

			int numberOfRead;
			if ((numberOfRead = mSerial.read(buffer)) > 0) {

				StringBuffer hexBuffer = new StringBuffer();
				for (int i = 0; i < numberOfRead; i++) {
					hexBuffer.append((char) (buffer[i] & 0xFF));
				}

				mReadPane.append(hexBuffer.toString());

				mReadCounter += numberOfRead;
				mReadBytes.setText("" + mReadCounter);
			}
			
			mHandler.postDelayed(mReadThread, 10);
		}
	};
}
