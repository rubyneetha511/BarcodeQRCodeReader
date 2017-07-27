package com.project.scanner.data;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.project.scanner.R;
import com.project.scanner.barcode.BarcodeCaptureActivity;
import com.project.scanner.camera.CameraSourcePreview;

public class DataActivity extends AppCompatActivity {
    private static final String LOG_TAG = DataActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    private Barcode barcode;
    private CameraSourcePreview cameraSourcePreview;

    private TextView mCodeValueView;
    private TextView mFormatView;
    private TextView mTypeView;
    private SurfaceView mCameraSurfaceView;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        dbHelper = new DBHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        mCodeValueView = (TextView) findViewById(R.id.camera_result_value);
        mFormatView = (TextView) findViewById(R.id.camera_result_format);
        mTypeView = (TextView) findViewById(R.id.camera_result_type);

        if (savedInstanceState == null) {
            if(getIntent() != null && getIntent().getExtras() != null) {
                if (getIntent().getExtras().get("Barcode") != null) {
                    barcode = (Barcode) getIntent().getExtras().get("Barcode");
                    mCodeValueView.setText(barcode.displayValue);
                    mFormatView.setText("Format : " + BarcodeCaptureActivity.formatString(barcode));
                    mTypeView.setText("Type   :" + BarcodeCaptureActivity.typeString(barcode));
                } else {
                    mCodeValueView.setText("NILL");
                    mFormatView.setText("Format :");
                    mTypeView.setText("Type   :");
                    //mCameraSurfaceView.setText("NILL");
                }
                if (getIntent().getExtras().get("CameraSourcePreview") != null) {
                    cameraSourcePreview = (CameraSourcePreview) getIntent().getExtras().get("CameraSourcePreview");
                } else {
                    //mCodeView.setText("NILL");

                }
                //mFormatView.setText(barcode.format);
                //mCameraSurfaceView = cameraSourcePreview.mSurfaceView;
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}
