package jp.ne.jinoxst.mat.itg.activity.dialog;

import jp.ne.jinoxst.mat.itg.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleDialogFragment extends DialogFragment {
    public static SimpleDialogFragment newInstance(String msg) {
        SimpleDialogFragment frag = new SimpleDialogFragment();
        Bundle args = new Bundle();
        args.putString("msg", msg);
        frag.setArguments(args);
        return frag;
    }

    public static SimpleDialogFragment newInstance(int title, String msg) {
        SimpleDialogFragment frag = new SimpleDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putString("msg", msg);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String msg = getArguments().getString("msg");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.simple_dialog, null);
        TextView content = (TextView)layout.findViewById(R.id.simple_dialog_content);
        content.setText(msg);
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        Button closeButton = (Button)layout.findViewById(R.id.button_simple_dialog_ok);
        closeButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }
}
