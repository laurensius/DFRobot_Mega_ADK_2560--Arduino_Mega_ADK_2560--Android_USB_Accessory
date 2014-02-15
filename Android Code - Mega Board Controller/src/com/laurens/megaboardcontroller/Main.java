package com.laurens.megaboardcontroller;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import android.hardware.usb.*;


public class Main extends Activity {
	
	static final String TAG = "Mega Board Controller";
	static final String ACTION_USB_PERMISSION = "com.laurens.megaboardcontroler.USB_PERMISSION";
	PendingIntent mPermissionIntent;
	boolean mPermissionRequestPending;
	
	UsbManager mUsbManager;
	UsbAccessory mAccessory;
	ParcelFileDescriptor mFileDescriptor;
	FileInputStream mInputStream;
	FileOutputStream mOutputStream;
	
	final byte COMMAND_TEXT = 0x0;
	final byte TARGET_LED = 0x1;
	final byte TARGET_SERVO = 0x2;
	final byte TARGET_GRIPPER = 0x3;
	
	
	static final String LAMPU_1_ON = "LAMPU 1 ON";
	static final String LAMPU_1_OFF = "LAMPU 1 OFF"; 
	static final String LAMPU_2_ON = "LAMPU 2 ON";
	static final String LAMPU_2_OFF = "LAMPU 2 OFF"; 
	static final String LAMPU_3_ON = "LAMPU 3 ON";
	static final String LAMPU_3_OFF = "LAMPU 3 OFF"; 
	
	
	Button btnExitYa, btnExitTidak;
	TextView myTv,tvServo,tvServo2;	
	Dialog dialogExit;
	SeekBar seekServo,seekServo2;
	ToggleButton tbLampu1,tbLampu2,tbLampu3;
	
	
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d(TAG, "permission denied for accessory " + accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				if (accessory != null && accessory.equals(mAccessory)) {
					closeAccessory();
				}
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "In onCreate()");
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);
		setContentView(R.layout.activity_main);
		tvServo = (TextView)findViewById(R.id.tvServo);
		tvServo2 = (TextView)findViewById(R.id.tvServo2);
		seekServo = (SeekBar)findViewById(R.id.seekServo);
		seekServo2 = (SeekBar)findViewById(R.id.seekServo2); 
		tbLampu1 = (ToggleButton)findViewById(R.id.btnLampu_1);
		tbLampu2 = (ToggleButton)findViewById(R.id.btnLampu_2);
		tbLampu3 = (ToggleButton)findViewById(R.id.btnLampu_3);
		
		tbLampu1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tbLampu1.isChecked()){
					sendText(COMMAND_TEXT,TARGET_LED,LAMPU_1_ON);
					Toast.makeText(getApplicationContext(), "Lampu 1 dinyalakan", Toast.LENGTH_SHORT).show();
				}else{
					sendText(COMMAND_TEXT,TARGET_LED,LAMPU_1_OFF);
					Toast.makeText(getApplicationContext(), "Lampu 1 dimatikan", Toast.LENGTH_SHORT).show();
				}
			}
		});
		tbLampu2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tbLampu2.isChecked()){
					sendText(COMMAND_TEXT,TARGET_LED,LAMPU_2_ON);
					Toast.makeText(getApplicationContext(), "Lampu 2 dinyalakan", Toast.LENGTH_SHORT).show();
				}else{
					sendText(COMMAND_TEXT,TARGET_LED,LAMPU_2_OFF);
					Toast.makeText(getApplicationContext(), "Lampu 2 dimatikan", Toast.LENGTH_SHORT).show();
				}
			}
		});
		tbLampu3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tbLampu3.isChecked()){
					sendText(COMMAND_TEXT,TARGET_LED,LAMPU_3_ON);
					Toast.makeText(getApplicationContext(), "Lampu 3 dinyalakan", Toast.LENGTH_SHORT).show();
				}else{
					sendText(COMMAND_TEXT,TARGET_LED,LAMPU_3_OFF);
					Toast.makeText(getApplicationContext(), "Lampu 3 dimatikan", Toast.LENGTH_SHORT).show();
				}
			}
		});
		seekServo.setProgress(0);
	    seekServo.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	           progress = progress + 1;
	           String Servo = Integer.toString(progress);
	           sendText(COMMAND_TEXT,TARGET_SERVO,Servo);
	           tvServo.setText("ARM : posisi saat ini " + progress  + " derajat");
	        }

	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {}

	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {}

	    });
	    seekServo2.setProgress(0);
	    seekServo2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	           progress = progress + 1;
	           String Servo = Integer.toString(progress);
	           sendText(COMMAND_TEXT,TARGET_GRIPPER,Servo);
	           tvServo2.setText("Gripper : posisi saat ini " + progress  + " derajat");
	        }

	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {}

	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {}

	    });
		
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mInputStream != null && mOutputStream != null) {
			return;
		}
		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		closeAccessory();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mUsbReceiver);
	}
	
	
/*----------------------------------- ----------------------------------------------------------*/	
	private void openAccessory(UsbAccessory accessory) {
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Log.d(TAG, "accessory opened");
		} else {
			Log.d(TAG, "accessory open fail");
		}
	}
	
	private void closeAccessory() {
		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
		}
	}
	
	public void sendText(byte command,byte target,String text){
		int panjang = text.length();
		byte[] buffer = new byte[3 + panjang];
		if(panjang <= 252){
			buffer[0] = command;
			buffer[1] = target;
			buffer[2] = (byte)panjang;
			byte[] textDalamByte = text.getBytes();
			for(int x=0;x<panjang;x++){
				buffer[3+x] = textDalamByte[x];
			}
			if(mOutputStream!=null){
				try{
					mOutputStream.write(buffer);
				}catch(IOException e){
					Log.e(TAG, "Kirim data gagal : ", e);
				}
			}
		}
	}
	
	public void tanyaExit(){
		dialogExit = new Dialog(Main.this);
		dialogExit.setContentView(R.layout.activity_dialogexit);
		dialogExit.setTitle("Konfirmasi Keluar");
		btnExitYa = (Button) dialogExit.findViewById(R.id.btnExitYa);
		btnExitTidak = (Button) dialogExit.findViewById(R.id.btnExitTidak);
		dialogExit.show();
		
		btnExitYa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeAccessory();
				unregisterReceiver(mUsbReceiver);
			}
		});
		
		btnExitTidak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogExit.dismiss();
			}
		});
	}
}