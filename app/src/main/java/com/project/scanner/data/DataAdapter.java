package com.project.scanner.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.project.scanner.R;
import com.project.scanner.barcode.BarcodeCaptureActivity;
import com.project.scanner.util.Constants;
import com.project.scanner.util.ScannerUtil;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.BarcodeHolder>{

    public List<BarcodeData> history_data;

    private static Context context;

    public static DBHelper dbhelper;

    public DataAdapter(List<BarcodeData> history_data, DBHelper dbhelper, Context context) {
        this.history_data = history_data;
        this.dbhelper = dbhelper;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return history_data.size();
    }

    @Override
    public BarcodeHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        BarcodeHolder holder = new BarcodeHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(BarcodeHolder holder, final int i) {
        holder.barcodeType.setText(history_data.get(i).getType());
        holder.barcodeValue.setText(history_data.get(i).getDisplayValue());
        holder.barcodeDate.setText(history_data.get(i).getDate());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //setPosition(holder.getPosition());
                return false;
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class BarcodeHolder extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener {

        CardView cv;
        TextView barcodeType;
        TextView barcodeValue;
        TextView barcodeDate;
        ImageButton imageButton;
        ImageButton threeeDotsButton;

        BarcodeHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            barcodeType = (TextView) itemView.findViewById(R.id.type);
            barcodeValue = (TextView)itemView.findViewById(R.id.displayValue);
            barcodeDate = (TextView)itemView.findViewById(R.id.date);

            //imageButton = (ImageButton)itemView.findViewById(R.id.button_image);
            //imageButton.setBackground(R.drawable.new_todo_image);

            threeeDotsButton = (ImageButton)itemView.findViewById(R.id.button_three_dots);
            threeeDotsButton.setOnCreateContextMenuListener(this);
            threeeDotsButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // profit
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            //menuInfo is null
            menu.add(Menu.NONE, R.id.item_delete, Menu.NONE, "Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    dbhelper.deleteHistory(barcodeValue.getText().toString());
                    Toast.makeText(context, "Successfully deleted", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, HistoryActivity.class);
                    context.startActivity(intent);

                    return true;
                }
            });;
//            menu.add(Menu.NONE, R.id.item_share, Menu.NONE, "Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//
//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    sharingIntent.setType("image/jpeg");
//                    StringBuilder shareBody = new StringBuilder();
//                    String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                            ScannerUtil.getBitmap(barcodeValue.getText(), barcodeValue.getText(), 100, 100), "title", null);
//                    Uri bitmapUri = Uri.parse(bitmapPath);
//
//                    sharingIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
//                    shareBody.append(barcodeData.getDisplayValue());
//
//                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Barcode/QR Code");
//                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody.toString());
//
//                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
//
//                    return true;
//                }
//            });
        }

    }
}
