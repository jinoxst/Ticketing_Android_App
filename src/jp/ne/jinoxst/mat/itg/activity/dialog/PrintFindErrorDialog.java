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

public class PrintFindErrorDialog extends DialogFragment {
    public static PrintFindErrorDialog newInstance(String... s) {
        PrintFindErrorDialog frag = new PrintFindErrorDialog();
        Bundle args = new Bundle();
        args.putStringArray("str", s);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String str[] = getArguments().getStringArray("str");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.printer_find_error_dialog, null);
        TextView titleTextView = (TextView)layout.findViewById(R.id.title_textview);
        titleTextView.setText(str[0]);
        TextView msgTextView = (TextView)layout.findViewById(R.id.msg_textview);
        if(str.length == 2){
            msgTextView.setText(str[1]);
        }else if(str.length == 3){
            msgTextView.setText(str[1].replaceAll("#1#", str[2]));
        }
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        Button closeButton = (Button)layout.findViewById(R.id.button_dialog_print_close);
        closeButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }
}
