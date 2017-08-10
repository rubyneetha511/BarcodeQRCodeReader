package com.project.scanner.barcode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.project.scanner.R;
import com.project.scanner.camera.CameraSource;
import com.project.scanner.camera.CameraSourcePreview;
import com.project.scanner.data.BarcodeData;
import com.project.scanner.data.DBHelper;
import com.project.scanner.data.DataActivity;
import com.project.scanner.data.HistoryActivity;
import com.project.scanner.settings.SettingsActivity;
import com.project.scanner.util.Constants;
import com.project.scanner.util.ScannerUtil;

import java.io.IOException;

public final class BarcodeCaptureActivity extends AppCompatActivity
        implements BarcodeTracker.BarcodeGraphicTrackerCallback, NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "Barcode-reader";
    private static final int REQUEST_CODE = 1;

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String BarcodeObject = "BarcodeData";

    // Constants used to pass extra data in the intent
    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;

    // Constants used to pass extra data in the intent
    private DBHelper dbhelper;
    private ImageView imageView;
    private FloatingActionButton flashButtonView;
    private boolean isFlashOn = false;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_navigation);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);

        dbhelper = new DBHelper(getApplicationContext());

//        flashButtonView = (FloatingActionButton) findViewById(R.id.flashButton);
        boolean hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(BarcodeCaptureActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    //finish();
                }
            });
            alert.show();
        }

        boolean autoFocus = true;

//        flashButtonView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                isFlashOn = !isFlashOn;//
////                if (isFlashOn) {
////                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
////
////                } else {
////                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
////
////                }
//
//            }
//        });


        imageView = (ImageView) this.findViewById(R.id.camera_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file), Context.MODE_PRIVATE);

        boolean appCameraAccess = sharedPref.getBoolean(getString(R.string.pref_allow_camera_access), true);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED && appCameraAccess ) {
            createCameraSource(autoFocus, isFlashOn);
        } else {
            requestCameraPermission();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onDetectedQrCode(Barcode barcode) {
        if (barcode != null) {
            Intent intent = new Intent(getApplicationContext(), DataActivity.class);
            intent.putExtra(Constants.DATA_VALUE, barcode.displayValue);
            intent.putExtra(Constants.DATA_FORMAT, barcode.format);
            intent.putExtra(Constants.DATA_TYPE, ScannerUtil.getBarcodeType(barcode.valueFormat));
            intent.putExtra(Constants.BITMAP, ScannerUtil.getBitmap(barcode.displayValue, barcode.format, 100, 100));

            setResult(CommonStatusCodes.SUCCESS, intent);
            startActivity(intent);

            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                    getString(R.string.preference_file), Context.MODE_PRIVATE);

            if (sharedPref.getBoolean(getString(R.string.pref_add_to_history), true)) {
                dbhelper.addHistory(new BarcodeData(barcode));
                Toast.makeText(BarcodeCaptureActivity.this, "Saved", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(BarcodeCaptureActivity.this, "Not Saved", Toast.LENGTH_LONG).show();
            }

            finish();
        }
    }

    // Handles the requesting of the camera permission.
    private void requestCameraPermission() {
        Log.w(LOG_TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
        }
    }

    /**
     * Creates and starts the camera.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(this);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(LOG_TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error,
                        Toast.LENGTH_LONG).show();
                Log.w(LOG_TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(metrics.widthPixels, metrics.heightPixels)
                .setRequestedFps(24.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_ON : Camera.Parameters.FLASH_MODE_OFF)
                .build();

    }

    // Restarts the camera
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    // Stops the camera
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(LOG_TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = true;
            boolean useFlash = false;
            createCameraSource(autoFocus, isFlashOn);
            return;
        }

        Log.e(LOG_TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scan) {

            // Do nothing. Just close teh drawer

        } else if (id == R.id.nav_history) {

            Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {

            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {

            StringBuilder message = new StringBuilder();
            message.append("Version: 1.0\n");
            message.append("Authors: \n");
            message.append("Neetha Prabhu \n");
            message.append("Sai Sivakanth Vadapalli");
            new AlertDialog.Builder(this).setMessage(message.toString())
                    .setTitle(R.string.app_name)
                    .setCancelable(false)
                    .setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton){
                                    finish();
                                }
                            })
                    .show();

        } else if (id == R.id.nav_exit) {
            Toast.makeText(BarcodeCaptureActivity.this, "Closing the application", Toast.LENGTH_LONG).show();
            this.finishAffinity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {

                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    //mResultTextView.setText(barcode.displayValue);

                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(photo);


                } else {
                 //   mResultTextView.setText(R.string.no_barcode_captured);
                }
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    public void handleResult(final Barcode barcode) {
        // Do something with the result here

        Log.e("handler", barcode.displayValue); // Prints scan results
        MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.beep);
        mediaPlayer.start();

        // show the scanner result into dialog box.
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(barcode.displayValue);
        builder.setPositiveButton("Share ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, barcode.displayValue);
                //+"\n"+rawResult.getBarcodeFormat().toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("Save ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dbhelper.addHistory(db,barcode.getText(),rawResult.getBarcodeFormat().toString());
            }
        });

        android.support.v7.app.AlertDialog alert1 = builder.create();
        alert1.show();

        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);
    }

//    /*
//    * Turning On flash
//    */
//    private void turnOnFlash() {
//        if (!isFlashOn) {
//            params = camera.getParameters();
//            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
//            camera.setParameters(params);
//            camera.startPreview();
//            isFlashOn = true;
//
//            // changing button/switch image
//            toggleButtonImage();
//        }
//
//    }
//
//    /*
//    * Turning Off flash
//    */
//    private void turnOffFlash() {
//        if (isFlashOn) {
//            if (camera == null || params == null) {
//                return;
//            }
//            // play sound
//            playSound();
//
//            params = camera.getParameters();
//            params.setFlashMode(Parameters.FLASH_MODE_OFF);
//            camera.setParameters(params);
//            camera.stopPreview();
//            isFlashOn = false;
//
//            // changing button/switch image
//            toggleButtonImage();
//        }
//    }
//
//    /*
//    * Toggle switch button images
//    * changing image states to on / off
//    * */
//    private void toggleButtonImage(){
//        if(isFlashOn){
//            btnSwitch.setImageResource(R.drawable.btn_switch_on);
//        }else{
//            btnSwitch.setImageResource(R.drawable.btn_switch_off);
//        }
//    }
}
