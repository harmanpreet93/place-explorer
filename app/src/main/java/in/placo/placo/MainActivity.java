package in.placo.placo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    protected static final String TAG = "Landing Page";

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 60000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    protected double latitude = -1,longitude = -1;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    private ProgressDialog pDialog;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestingLocationUpdates = false;
        pDialog = new ProgressDialog(this);
//        getCurrentLocation = (LinearLayout) findViewById(R.id.get_current_location);
//        getCurrentLocation.setOnClickListener(this);

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(this, "No Internet Connection","You don't have internet connection.");
        }

        buildGoogleApiClient();
        getCurrentLocation();

        CardView shopCard = (CardView) findViewById(R.id.shop_card);
        CardView restaurantCard = (CardView) findViewById(R.id.restaurant_card);
        CardView nightLifeCard = (CardView) findViewById(R.id.night_club_card);
        CardView checkinCard = (CardView) findViewById(R.id.checkin_card);

        if (shopCard != null) {
            shopCard.setOnClickListener(this);
        }
        if (restaurantCard != null) {
            restaurantCard.setOnClickListener(this);
        }
        if (nightLifeCard != null) {
            nightLifeCard.setOnClickListener(this);
        }

        if (checkinCard != null) {
            checkinCard.setOnClickListener(this);
        }

    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        // Setting OK Button
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    // The system calls this method as the first indication that
    // the user is leaving the fragment (though it does not always mean the fragment is being destroyed).
    // This is usually where you should commit any changes that should be persisted beyond
    // the current user session (because the user might not come back)
    @Override
    public void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
//        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        /*
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        */
    }

    @Override
    public void onBackPressed() {

//        if (mCurrentLocation != null) {
//            Log.v("wtf","In onBackPressed method of MyLocation Activity with current location not null");
//            int resultCode = 100;
//
//            HashMap<String, String> currentLocation = resolveAddress(latitude, longitude);
//            String locationString = "";
//
//            if (currentLocation != null) {
//                locationString = currentLocation.get("city") + ", " + currentLocation.get("state");
//            }
//
//            Intent intent = new Intent();
//            intent.putExtra("CURRENT_LOCATION", locationString);
//            setResult(resultCode, intent);
//        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                Log.v("wtf","Permissions not satisfied");

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            } else {
                Log.v("wtf", "In onConnected, all permissions granted");
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                updateLocationUI();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateLocationUI();
    }

    public void getCurrentLocation() {
        createAlertDialogBox("Fetching Location...");
        // First check availability of play services
        if (checkPlayServices()) {
            Log.v("wtf", "play services are fine");
            // Building the GoogleApi client
            // buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();
            checkLocationSettings();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shop_card:
                openNewActivity(CategoryActivity.class,"shop");
                break;

            case R.id.restaurant_card:
                openNewActivity(CategoryActivity.class,"restaurant");
                break;

            case R.id.night_club_card:
                openNewActivity(CategoryActivity.class,"night_club");
                break;

            case R.id.checkin_card:
            openNewActivity(CheckInActivity.class,"");
            break;
        }

    }

    private void openNewActivity(Class activityName,String category) {
        if (longitude != -1 && latitude != -1) {
            Log.v("wtf","opening category list");
            Intent intent = new Intent(this,activityName);
            intent.putExtra("category",category);
            intent.putExtra("location",latitude + "," + longitude);
//            longitude = -1;
//            latitude = -1;
            startActivity(intent);
        }
        else {
            getCurrentLocation();
        }

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

        final Status status = locationSettingsResult.getStatus();

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied");
                startLocationUpdates();
                pDialog.dismiss();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "MyLocation settings are not satisfied. Show the user a dialog to upgrade location settings");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    pDialog.dismiss();
                    Log.v(TAG, "PendingIntent unable to execute request: " + e.toString());
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                pDialog.dismiss();
                Log.v(TAG, "MyLocation settings are inadequate, and cannot be fixed here. Dialog not created");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        pDialog.dismiss();

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Log.d("wtf", "Ok");
                        createAlertDialogBox("Fetching Location...");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.d("wtf", "Canceled");
                        longitude = -1;
                        latitude = -1;
                        break;
                    default:
                        Log.d("wtf", "Default");
                        break;
                }
                break;
        }
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this.getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                this.finish();
            }
            pDialog.dismiss();
            return false;
        }
        return true;
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
        } else {
            Log.v("wtf", "Inside startLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    mRequestingLocationUpdates = true;
                    Log.v("wtf", "Inside startLocationUpdates, in resultcallback");
                }
            });
        }

    }

    /**
     * Sets the value of the UI fields for the location latitude, longitude
     */
    private void updateLocationUI() {

        if (mCurrentLocation != null) {

            Log.v("wtf", "mCurrentLocation extras: " + mCurrentLocation.toString());
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
            pDialog.dismiss();

        } else {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            Log.v("wtf", "mCurrentLocation is null");
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
                // setButtonsEnabledState();
            }
        });
    }

    private void createAlertDialogBox(String message) {
        pDialog.setMessage(message);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }


}
