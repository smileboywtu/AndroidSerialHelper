package com.smileboy.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class AndroidCameraUtil {

	public static final int REQUEST_IMAGE_CAPTURE = 1;
	public static final String JPEG_SUFFIX = ".jpg";

	private Activity activity = null;
	private String mMediaFile;

	public AndroidCameraUtil(Activity activity) {

		this.activity = activity;
	}
	
	public String getImageFilePath(){
		return mMediaFile;
	}

	public void dispatchTakePictureIntent() {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
			Uri fileUri = getOutputMediaFileUri();
			mMediaFile = fileUri.getPath();
			takePictureIntent.putExtra(
					MediaStore.EXTRA_OUTPUT, 
					fileUri);
			activity.startActivityForResult(
					takePictureIntent, 
					REQUEST_IMAGE_CAPTURE);
		}
	}

	private File getOutputMediaFile() {

		File mediaStorageDir = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"LTCodeHelper");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("LTCodeHelper", "failed to create directory");
				return null;
			}
		}
		
		// build the file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File image = new File(
				mediaStorageDir.getPath() 
				+ File.separator 
				+ "IMG_" 
				+ timeStamp 
				+ ".jpg");

		return image;
	}

	private Uri getOutputMediaFileUri(){
		return Uri.fromFile(getOutputMediaFile());
	}
}
