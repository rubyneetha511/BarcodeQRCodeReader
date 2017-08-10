package com.project.scanner.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.project.scanner.R;
import com.project.scanner.util.Constants;
import com.project.scanner.util.ScannerUtil;

import java.util.List;


public class HistoryActivity extends AppCompatActivity {

    public DBHelper dbhelper;
    public RecyclerView recyclerView;
    public DataAdapter recycler_adapter;
    public List<BarcodeData> listHistory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = (RecyclerView) findViewById(R.id.rec);

        dbhelper = new DBHelper(getApplicationContext());

        listHistory = dbhelper.getAllHistory();

        if(listHistory.isEmpty()) {
            Toast.makeText(HistoryActivity.this, "History is empty", Toast.LENGTH_LONG).show();
        }
        recycler_adapter = new DataAdapter(listHistory, dbhelper, getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recycler_adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new HistoryActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {

//                Toast.makeText(HistoryActivity.this, "Single Click on position :" + position, Toast.LENGTH_SHORT).show();

                BarcodeData barcodeData = listHistory.get(position);
                if(barcodeData != null) {

                    Intent intent = new Intent(getApplicationContext(), DataActivity.class);
                    intent.putExtra(Constants.DATA_VALUE, barcodeData.getDisplayValue());
                    intent.putExtra(Constants.DATA_FORMAT, barcodeData.getFormat());
                    intent.putExtra(Constants.DATA_TYPE, barcodeData.getType());
                    intent.putExtra(Constants.BITMAP, ScannerUtil.getBitmap(barcodeData.getDisplayValue(), barcodeData.getFormat(), 100, 100));

                    setResult(CommonStatusCodes.SUCCESS, intent);
                    startActivity(intent);

                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

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

}