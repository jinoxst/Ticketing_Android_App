package jp.ne.jinoxst.mat.itg.activity.adapter;

import java.util.List;

import jp.ne.jinoxst.mat.itg.R;
import jp.ne.jinoxst.mat.itg.pojo.OrderLeftMenu;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LeftMenuListAdapter extends ArrayAdapter<OrderLeftMenu> {
    private final LayoutInflater mInflater;
    public LeftMenuListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public LeftMenuListAdapter(Context context, int resource, List<OrderLeftMenu> leftmenus) {
        super(context, resource, leftmenus);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = mInflater.inflate(R.layout.leftmenu_unit, parent, false);
        }

        OrderLeftMenu lm = getItem(position);
        if (lm != null) {
            TextView tv = (TextView) v.findViewById(R.id.leftmenu_text1);
            if (tv != null) {
                tv.setText(lm.getTitle());
            }
        }
        return v;
    }
}