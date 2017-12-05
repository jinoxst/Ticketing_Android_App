package jp.ne.jinoxst.mat.itg.activity.adapter;

import java.util.List;

import jp.ne.jinoxst.mat.itg.R;
import jp.ne.jinoxst.mat.itg.pojo.Ticket;
import jp.ne.jinoxst.mat.itg.util.StringUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TicketListAdapter extends ArrayAdapter<Ticket> {
    private final LayoutInflater mInflater;
    private Activity activity;
    public TicketListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public TicketListAdapter(Context context, int resource, List<Ticket> leftmenus) {
        super(context, resource, leftmenus);
        activity = (Activity)context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = mInflater.inflate(R.layout.ticket_unit, parent, false);
        }

        Ticket ticket = getItem(position);
        if (ticket != null) {
            ImageView imageV = (ImageView)v.findViewById(R.id.brand_image);
            Bitmap itembitmap = BitmapFactory.decodeFile(activity.getFilesDir().getPath()+"/"+ticket.getBrandImg());
            imageV.setImageBitmap(itembitmap);

            TextView tp = (TextView) v.findViewById(R.id.ticket_price);
            tp.setText(activity.getResources().getString(R.string.yen_mark)+StringUtil.getCurrencyFormat(ticket.getEmmPrice()));
        }
        return v;
    }
}