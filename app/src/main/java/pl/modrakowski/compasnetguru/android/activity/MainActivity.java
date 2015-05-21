package pl.modrakowski.compasnetguru.android.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.androidannotations.annotations.*;
import pl.modrakowski.compasnetguru.R;
import pl.modrakowski.compasnetguru.android.view.CompassView;
import pl.modrakowski.compasnetguru.util.Logger;

import static java.lang.Math.*;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

	private static final String DISTANCE_LABEL_PATTERN = "Distance[km]: %2.2f";
	private static final String BEARING_LABEL_PATTERN = "Bearing: %2.2f";

	// Testing values for Chad.
	private static final double LAT_CHAD = 12.463396f;
	private static final double LNG_CHAD = 19.511719f;

	private static final float KILO = 1000f;

	@SystemService SensorManager sensorManager;
	@SystemService LocationManager locationManager;

	@ViewById(R.id.mainAzimuthValue) TextView azimuthValue;
	@ViewById(R.id.mainCompasView) CompassView compassView;
	@ViewById(R.id.mainBearing) TextView bearing;
	@ViewById(R.id.mainDistance) TextView distance;
	@ViewById(R.id.mainLocationLatInput) EditText inputLat;
	@ViewById(R.id.mainLocationLngInput) EditText inputLng;

	private final float[] orientationValuesHolder = new float[3];
	private final float[] rotationMatrix = new float[9];

	private float[] gravity;
	private float[] magnetic;

	private Location providedLocation;

	@AfterViews void afterViews() {
		locationManager.requestLocationUpdates(1, 1f, createCriteria(), this, null);

		providedLocation = new Location(LocationManager.GPS_PROVIDER);
		providedLocation.setLatitude(LAT_CHAD);
		providedLocation.setLongitude(LNG_CHAD);
	}

	@Click(R.id.mainStartTracking) void onStartTracking() {
		final String inputValueLat = inputLat.getText().toString();
		final String inputValueLng = inputLng.getText().toString();
		final boolean isProperInput = !TextUtils.isEmpty(inputValueLat)
				&& !TextUtils.isEmpty(inputValueLng);

		if (isProperInput) {
			final float lat = Float.parseFloat(inputValueLat);
			final float lng = Float.parseFloat(inputValueLng);

			providedLocation.setLatitude(lat);
			providedLocation.setLongitude(lng);
		} else {
			Toast.makeText(this, "Provide proper values like: lat, lng",
					Toast.LENGTH_SHORT).show();
		}

		locationManager.requestLocationUpdates(1, 1f, createCriteria(), this, null);
	}

	@Override public void onSensorChanged(SensorEvent sensorEvent) {
		if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
			gravity = sensorEvent.values.clone();
		} else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			magnetic = sensorEvent.values.clone();
		}

		if (gravity != null && magnetic != null) {
			if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, magnetic)) {
				SensorManager.getOrientation(rotationMatrix, orientationValuesHolder);
				final double azimuthRad = orientationValuesHolder[0] - toRadians(90);

				bearingGlobalValue += azimuthRad;

				if (bearingGlobalCounter % 2 == 0) {
					compassView.setDegreeRad(bearingGlobalValue / bearingGlobalCounter);
					bearingGlobalValue = 0;
					bearingGlobalCounter = 0;
				}

				bearingGlobalCounter++;
			}
		}
	}

	@Override public void onAccuracyChanged(Sensor sensor, int i) {
		// Not needed.
	}

	@Override protected void onResume() {
		super.onResume();

		Sensor magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		Sensor gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		sensorManager.registerListener(this, magnetic, 2);
		sensorManager.registerListener(this, gravity, 2);
	}

	@Override protected void onStop() {
		sensorManager.unregisterListener(this);
		super.onStop();
	}

	private int bearingGlobalCounter;
	private float bearingGlobalValue;

	@Override public void onLocationChanged(Location location) {
		Logger.l("onLocationChanged");

		final float[] values = new float[3];
		Location.distanceBetween(location.getLatitude(), location.getLongitude(),
				providedLocation.getLatitude(), providedLocation.getLongitude(), values);

		final float distanceKm = values[0] / KILO;
		final float bearingValue = values[1];

		distance.setText(String.format(DISTANCE_LABEL_PATTERN, distanceKm));
		bearing.setText(String.format(BEARING_LABEL_PATTERN, bearingValue));

		compassView.setTargetDegree(toRadians(bearingValue));
	}

	private Criteria createCriteria() {
		final Criteria result = new Criteria();
		result.setAccuracy(Criteria.ACCURACY_LOW);
		return result;
	}

	@Override public void onStatusChanged(String s, int i, Bundle bundle) {
		// Not needed.
	}

	@Override public void onProviderEnabled(String s) {
		// Not needed.
	}

	@Override public void onProviderDisabled(String s) {
		// Not needed.
	}
}
