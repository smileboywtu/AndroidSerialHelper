package com.smileboy.lthelper;

import java.io.File;
import java.util.List;

import com.example.lthelper.R;
import com.smileboy.utils.AndroidCameraUtil;
import com.smileboy.utils.GeneralSerialUtil;
import com.smileboy.utils.LubyCodeHelper;
import com.smileboy.utils.StatusDialog;
import com.smileboy.utils.UartConfigure;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import tw.com.prolific.driver.pl2303.PL2303Driver;

public class MainActivity extends Activity {

	private static final String ACTION_USB_PERMISSION = "com.prolific.pl2303hxdsimpletest.USB_PERMISSION";

	private static final String PROMPT = "smile#: ";

	// member
	private UartConfigure mConfig;
	private PL2303Driver mSerial;
	private ScrollView mScrollPane;
	private TextView mMessagePane;

	private String mSendFile;
	private AndroidCameraUtil mCameraIntent;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009688")));

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*************************************************************
		 * set up camera
		 ************************************************************/
		if (isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE)) {
			mSendFile = "";
			mCameraIntent = new AndroidCameraUtil(MainActivity.this);
		} else {
			mCameraIntent = null;
		}

		/*************************************************************
		 * set up message panel
		 ************************************************************/
		mScrollPane = (ScrollView) findViewById(R.id.scroll);
		mMessagePane = (TextView) findViewById(R.id.output);

		/*********************************************************
		 * set up the service
		 ********************************************************/
		mConfig = new UartConfigure();
		mSerial = new PL2303Driver((UsbManager) getSystemService(Context.USB_SERVICE), MainActivity.this,
				ACTION_USB_PERMISSION);

		if (!mSerial.PL2303USBFeatureSupported()) {

			String text = "No Support USB host API";
			systemMessage(text);

			mSerial = null;
		}

		/*********************************************************
		 * set up handler
		 */
		mHandler = new Handler();
	}

	@Override
	protected void onStart() {

		mHandler.post(mDetectDevice);

		super.onStart();
	}

	@Override
	protected void onResume() {

		if (null == mSerial) {
			mConfig.uart_state_str = "No USB Host";
			systemMessage("No Support USB Host API");
		} else if (!mSerial.isConnected()) {
			if (!mSerial.enumerate()) {
				mConfig.uart_state_str = "No Device";
				systemMessage("No Device");
			} else {
				mConfig.uart_state_str = "Find Device";
				systemMessage("Find Device");
			}
		} else {
			mConfig.uart_state_str = "Device Connected";
		}

		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {

		if (null != mSerial) {
			mSerial.end();
			mSerial = null;
		}
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// deal with camera result
		if(AndroidCameraUtil.REQUEST_IMAGE_CAPTURE == requestCode){
			if(RESULT_OK == resultCode){
				mSendFile = mCameraIntent.getImageFilePath();
				File media = new File(mSendFile);
				long size = media.length();
				String name = media.getName();
				String text = "file saved successfully\n";
				text += "	file name: " + name + "\n";
				text += "	file size: " + size;
				systemMessage(text);
			}else{
				systemMessage("take photo failed");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		MenuItem rateMenuItem = menu.findItem(R.id.baudrate_action);
		Spinner rateSpinner = (Spinner) rateMenuItem.getActionView().findViewById(R.id.baudrate_sp);
		rateSpinner.setSelection(2);

		rateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mConfig.baud_rate_str = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {

		case R.id.open_action:
			openSerial();
			break;

		case R.id.camera_action:
			openCamera();
			break;
			
		case R.id.status_action:
			showStatus();
			break;
			
		case R.id.clear_action:
			clearWindow();
			break;
			
		case R.id.serial_action:
			generalSerial();
			break;
			
		case R.id.fountain_action:
			fountainCode();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void openSerial() {

		if (null != mSerial && mSerial.isConnected()) {

			PL2303Driver.BaudRate baudrate;

			switch (Integer.parseInt(mConfig.baud_rate_str)) {
			case 9600:
				baudrate = PL2303Driver.BaudRate.B9600;
				break;
			case 19200:
				baudrate = PL2303Driver.BaudRate.B19200;
				break;
			case 115200:
				baudrate = PL2303Driver.BaudRate.B115200;
				break;
			default:
				baudrate = PL2303Driver.BaudRate.B9600;
				break;
			}

			mSerial.InitByBaudRate(baudrate);
			String text = "setup serial with rate: " + mConfig.baud_rate_str;
			systemMessage(text);
		} else {
			String text = "can't open, no device connected";
			systemMessage(text);
		}
	}

	private void openCamera() {

		if (null == mCameraIntent) {
			systemMessage("camera intent not available");
		} else {
			systemMessage("open camera");
			mCameraIntent.dispatchTakePictureIntent();
		}
	}
	
	private void showStatus(){
		
		new StatusDialog(mConfig).show(getFragmentManager(), "status");
	}
	
	private void clearWindow(){
		
		mMessagePane.setText("");
		mScrollPane.fullScroll(View.FOCUS_DOWN);
	}
	
	private void generalSerial(){
		
		if(null == mSerial){
			systemMessage("No USB Host API");
			return;
		}
		
		if(!mSerial.isConnected()){
			systemMessage("Device Not Connected");
			return;
		}
		
		new GeneralSerialUtil(mSerial).show(getFragmentManager(), "serial");
	}
	
	private void fountainCode(){
		
		if(null == mSerial){
			systemMessage("No USB Host API");
			return;
		}
		
		if(!mSerial.isConnected()){
			systemMessage("Device Not Connected");
			return;
		}
		
		if(mSendFile.isEmpty()){
			systemMessage("load file first");
			return;
		}
		
		new LubyCodeHelper(mSerial, mSendFile).show(getFragmentManager(), "Luby");
	}

	/**
	 * @brief this runnable use to detect the device.
	 */
	private Runnable mDetectDevice = new Runnable() {

		@Override
		public void run() {

			if (null == mSerial) {
				return;
			}

			if (mSerial.isConnected()) {
				mConfig.uart_state_str = "Device Connected";
			} else {
				if (mSerial.enumerate()) {
					mConfig.uart_state_str = "Find Device";
				} else {
					mConfig.uart_state_str = "No Device";
				}
			}

			mHandler.postDelayed(mDetectDevice, 500);
		}
	};

	/**
	 * @brief check if there is intent to deal with this action
	 */
	private static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	/**
	 * 
	 * @param text
	 *            to output in terminal
	 */
	private void systemMessage(String text) {
		mMessagePane.append("\n" + PROMPT);
		mMessagePane.append(text);
		mScrollPane.fullScroll(View.FOCUS_DOWN);
	}

}
