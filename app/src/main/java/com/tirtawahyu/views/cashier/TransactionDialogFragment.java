package com.tirtawahyu.views.cashier;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tirtawahyu.R;
import com.tirtawahyu.util.Util;

public class TransactionDialogFragment extends AppCompatDialogFragment {
    private TransactionDialogListener listener;

    private EditText mEditText;
    private TextView tvTotal;
    private TextView tvChange;
    private Button positiveButton;

    private static TransactionDialogFragment INSTANCE;

    private int total;
    private int paymentAmount;

    public static TransactionDialogFragment newInstance(int total) {
        if (INSTANCE == null) {
            INSTANCE = new TransactionDialogFragment();
            INSTANCE.total = total;
            INSTANCE.paymentAmount = 0;
        }
        return INSTANCE;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (TransactionDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TransactionDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.fragment_transaction_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_transaction_dialog);
        builder.setPositiveButton(R.string.button_process, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onTransactionDialogSuccess(paymentAmount);
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.setView(view);

        Dialog dialog = builder.create();
        tvTotal = view.findViewById(R.id.tv_calculator_total);
        tvTotal.setText(Util.formatPrice(total));

        tvChange = view.findViewById(R.id.tv_calculator_change);

        mEditText = view.findViewById(R.id.et_payment_amount);
        mEditText.addTextChangedListener(createTextWatcher());

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();

        positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        return dialog;
    }

    private TextWatcher createTextWatcher() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    paymentAmount = Integer.parseInt(charSequence.toString());

                    int change = paymentAmount - total;
                    if (change < 0) {
                        mEditText.setError(getString(R.string.message_insufficient_funds));
                        tvChange.setText(R.string.dummy_price);
                        positiveButton.setEnabled(false);
                    } else {
                        tvChange.setText(Util.formatPrice(change));
                        positiveButton.setEnabled(true);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        return textWatcher;
    }

    public interface TransactionDialogListener {
        void onTransactionDialogSuccess(int paymentAmount);
    }
}
