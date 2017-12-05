package jp.ne.jinoxst.mat.itg.activity.dialog;

import jp.ne.jinoxst.mat.itg.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommonProgressDialog extends DialogFragment {
    public static CommonProgressDialog newInstance(String title) {
        CommonProgressDialog frag = new CommonProgressDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.common_progress_dialog, null);
        TextView titleView = (TextView)layout.findViewById(R.id.common_progress_textview);
        titleView.setText(title);
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }
}
