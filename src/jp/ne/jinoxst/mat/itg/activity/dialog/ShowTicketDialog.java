package jp.ne.jinoxst.mat.itg.activity.dialog;

import jp.ne.jinoxst.mat.itg.R;
import jp.ne.jinoxst.mat.itg.pojo.Ticket;
import jp.ne.jinoxst.mat.itg.util.StringUtil;
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

public class ShowTicketDialog extends DialogFragment {
    public static ShowTicketDialog newInstance(Ticket ticket) {
        ShowTicketDialog frag = new ShowTicketDialog();
        Bundle args = new Bundle();
        args.putSerializable("ticket", ticket);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Ticket ticket = (Ticket)getArguments().getSerializable("ticket");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.ticketlist_dialog, null);
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        TextView ticketContent = (TextView)layout.findViewById(R.id.ticket_content);
        ticketContent.setText(StringUtil.getReceiptContent(getResources(), ticket));
        Button closeButton = (Button)layout.findViewById(R.id.button_dialog_close);
        closeButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }
}
