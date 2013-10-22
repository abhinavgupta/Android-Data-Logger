package com.abhinav.androiddatalogger;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private SensorManager sensorManager;
	private BufferedWriter file;
	private LocationManager locationManager;
	private Map<Integer, String> sensorTypes = new HashMap<Integer, String>();
	private Map<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();
	private TextView filenameDisplay;
	private TextView logDisplay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get sensors to be captured
		sensorTypes.put(Sensor.TYPE_ACCELEROMETER, "ACCEL");
		sensorTypes.put(Sensor.TYPE_GYROSCOPE, "GYRO");
		sensorTypes.put(Sensor.TYPE_LINEAR_ACCELERATION, "LINEAR");
		sensorTypes.put(Sensor.TYPE_MAGNETIC_FIELD, "MAG");
		sensorTypes.put(Sensor.TYPE_GRAVITY, "GRAV");
		sensorTypes.put(Sensor.TYPE_ROTATION_VECTOR, "ROTATION");

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		for (Integer type : sensorTypes.keySet()) {
			sensors.put(type, sensorManager.getDefaultSensor(type));
		}

		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Register click listeners for buttons
		findViewById(R.id.toggle).setOnClickListener(clickListener);
		findViewById(R.id.btnEnter).setOnClickListener(clickListener);
		findViewById(R.id.btnExit).setOnClickListener(clickListener);

		filenameDisplay = (TextView) findViewById(R.id.filename);
		logDisplay = (TextView) findViewById(R.id.log);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopRecording();
	}

	private void startRecording() {
		// Prepare data storage
		File directory = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		String name = "AllData_" + System.currentTimeMillis() + ".csv";
		File filename = new File(directory, name);
		try {
			file = new BufferedWriter(new FileWriter(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		filenameDisplay.setText(name);

		// Register sensor listeners
		for (Sensor sensor : sensors.values()) {
			sensorManager.registerListener(sensorListener, sensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
	}

	private void stopRecording() {
		sensorManager.unregisterListener(sensorListener);
		locationManager.removeUpdates(locationListener);
		filenameDisplay.setText("");
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.toggle:
				if (((ToggleButton) v).isChecked()) {
					startRecording();
				} else {
					stopRecording();
				}
				break;
			case R.id.btnEnter:
				write("ENTER");
				break;
			case R.id.btnExit:
				write("EXIT");
				break;
			}
		}

	};

	private SensorEventListener sensorListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			write(sensorTypes.get(event.sensor.getType()), event.values);
		}

	};

	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			write("GPS",
					new double[] { location.getLatitude(),
							location.getLongitude() });
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void write(String tag, String[] values) {
		if (file == null) {
			return;
		}

		String line = "";
		if (values != null) {
			for (String value : values) {
				line += "," + value;
			}
		}
		line = Long.toString(System.currentTimeMillis()) + "," + tag + line
				+ "\n";

		try {
			file.write(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logDisplay.setText(line);
	}

	private void write(String tag, float[] values) {
		String[] array = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			array[i] = Float.toString(values[i]);
		}
		write(tag, array);
	}

	private void write(String tag, double[] values) {
		String[] array = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			array[i] = Double.toString(values[i]);
		}
		write(tag, array);
	}

	private void write(String tag) {
		write(tag, (String[]) null);
	}

}