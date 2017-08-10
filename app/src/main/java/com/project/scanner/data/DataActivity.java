package com.project.scanner.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.project.scanner.R;
import com.project.scanner.camera.CameraSourcePreview;
import com.project.scanner.util.Constants;
import com.project.scanner.util.ScannerUtil;

import java.util.EnumMap;
import java.util.Map;

public class DataActivity extends AppCompatActivity {
    private static final String LOG_TAG = DataActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    private DBHelper dbHelper;

    private CameraSourcePreview cameraSourcePreview;

    private TextView mCodeValueView;
    private TextView mFormatView;
    private TextView mTypeView;
    private ImageView mCameraView;

    private BarcodeData barcodeData;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        dbHelper = new DBHelper(getApplicationContext());

        createButtonOpen();
        createButtonShare();

        mCodeValueView = (TextView) findViewById(R.id.camera_result_value);
        mFormatView = (TextView) findViewById(R.id.camera_result_format);
        mTypeView = (TextView) findViewById(R.id.camera_result_type);
        mCameraView = (ImageView) findViewById(R.id.camera_view);

        barcodeData = new BarcodeData();

        if (savedInstanceState == null) {
            if(getIntent() != null && getIntent().getExtras() != null) {
                if (getIntent().getExtras().get(Constants.DATA_VALUE) != null) {
                    barcodeData.setDisplayValue((String) getIntent().getExtras().get(Constants.DATA_VALUE));
                }

                if (getIntent().getExtras().get(Constants.DATA_FORMAT) != null) {
                    barcodeData.setFormat((int) getIntent().getExtras().get(Constants.DATA_FORMAT));
                }

                if (getIntent().getExtras().get(Constants.DATA_TYPE) != null) {
                    barcodeData.setType((String) getIntent().getExtras().get(Constants.DATA_TYPE));
                }
                if (getIntent().getExtras().get(Constants.BITMAP) != null) {
                    barcodeData.setBitmap((Bitmap) getIntent().getExtras().get(Constants.BITMAP));

//                    Bitmap bitmap = getBitmap(barcodeData.getDisplayValue(), barcodeData.getFormat(), 100, 100);
//                    barcodeData.setBitmap(bitmap);
                }

                if (getIntent().getExtras().get("CameraSourcePreview") != null) {
                    cameraSourcePreview = (CameraSourcePreview) getIntent().getExtras().get("CameraSourcePreview");
                } else {
                    //mCodeView.setText("NILL");

                }

                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                        getString(R.string.preference_file), Context.MODE_PRIVATE);

                if(sharedPref.getBoolean(getString(R.string.pref_beep), true)) {
                    ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 300);
                }

                if(sharedPref.getBoolean(getString(R.string.pref_vibrate), true)) {
                    // Vibrate for 500 milliseconds
                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(1000);
                }

            }
        }

        mCodeValueView.setText(barcodeData.getDisplayValue());
        mFormatView.setText(ScannerUtil.getBarcodeFormat(barcodeData.getFormat()));
        mTypeView.setText(barcodeData.getType());

        mCameraView.setImageBitmap(barcodeData.getBitmap());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createButtonOpen() {
        Button openButton = (Button) findViewById(R.id.button_open);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                if (barcodeData.getType().equals("URL")) {
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(barcodeData.getDisplayValue()));
                } else if (barcodeData.getType().equals("PRODUCT")) {
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("http://www.google.com/#q=PRODUCT:" + barcodeData.getDisplayValue()));
                } else {
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("http://www.google.com/#q=" + barcodeData.getDisplayValue()));
                }

                startActivity(intent);
            }
        });

    }


    private void createButtonShare() {

        Button shareButton = (Button) findViewById(R.id.button_share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                StringBuilder shareBody = new StringBuilder();
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), barcodeData.getBitmap(),"title", null);
                Uri bitmapUri = Uri.parse(bitmapPath);

                sharingIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                shareBody.append(barcodeData.getDisplayValue());

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Barcode/QR Code");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody.toString());

                startActivity(Intent.createChooser(sharingIntent, "Share via"));

            }
        });
    }


}
