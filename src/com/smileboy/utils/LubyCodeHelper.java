package com.smileboy.utils;

import java.io.IOException;

import com.example.lthelper.R;

import LubyTransformCode.LTEncoder;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import tw.com.prolific.driver.pl2303.PL2303Driver;

public class LubyCodeHelper extends DialogFragment {

	private PL2303Driver mSerial;
	private LubyCodeConfig mConfig;
	private LubyCodeTask mLubyTask;

	private ImageButton mStartButton;
	private ImageButton mPauseButton;
	private ImageButton mStopButton;

	private TextView mCounter;
	private EditText mCValue;
	private EditText mDelta;

	public LubyCodeHelper(PL2303Driver serial, String filename) {
		mSerial = serial;
		mConfig = new LubyCodeConfig(filename);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_lubycode, null);

		mCounter = (TextView) view.findViewById(R.id.counter_tv);
		mCValue = (EditText) view.findViewById(R.id.cvalue_tv);
		mDelta = (EditText) view.findViewById(R.id.deltavalue_tv);

		mStartButton = (ImageButton) view.findViewById(R.id.ltstartbtn);
		mPauseButton = (ImageButton) view.findViewById(R.id.ltpausebtn);
		mStopButton = (ImageButton) view.findViewById(R.id.ltstopbtn);

		mStartButton.setOnClickListener(startCallback);
		mPauseButton.setOnClickListener(pauseCallback);
		mStopButton.setOnClickListener(stopCallback);

		builder.setView(view);
		builder.setTitle("Luby Transform Code");

		return builder.create();
	}

	private Button.OnClickListener startCallback = new OnClickListener() {

		@Override
		public void onClick(View v) {
			double current_c = Double.parseDouble(mCValue.getText().toString());
			double current_delta = Double.parseDouble(mDelta.getText().toString());
			mConfig.setC(current_c);
			mConfig.setDelta(current_delta);
			mConfig.resetCounter();
			mCounter.setText("" + mConfig.getCounter());

			// create task here
			if (null != mLubyTask && !mLubyTask.isCancelled()) {
				mLubyTask.cancel(true);
			}
			mLubyTask = new LubyCodeTask();
			mLubyTask.execute();
		}
	};

	private Button.OnClickListener pauseCallback = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};

	private Button.OnClickListener stopCallback = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!mLubyTask.isCancelled()) {
				mLubyTask.cancel(true);
			}
		}
	};

	public class LubyCodeTask extends AsyncTask<Void, Long, Void> {

		private LTEncoder mCoder;

		public LubyCodeTask() {
			mCoder = null;
		}

		@Override
		protected Void doInBackground(Void... params) {
			while (true) {
				// create the encoder first
				if (null == mCoder) {
					try {
						mCoder = new LTEncoder(
								mConfig.getImagefile(), 
								mConfig.getDelta(), 
								mConfig.getC());
					} catch (IOException e) {
						Toast.makeText(
								getActivity(), 
								"error with create Luby Encoder", 
								Toast.LENGTH_SHORT).show();
					}
				}
				// send package
				byte[] buffer = mCoder.getNextPackage();
				int len = mSerial.write(buffer);
				if (buffer.length == len) {
					mConfig.increaseCounter(1);
					publishProgress(mConfig.getCounter());
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			mCounter.setText("" + values[0]);
			super.onProgressUpdate(values);
		}

	}
}
