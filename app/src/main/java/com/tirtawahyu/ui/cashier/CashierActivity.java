package com.tirtawahyu.ui.cashier;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.tirtawahyu.R;
import com.tirtawahyu.model.Receipt;
import com.tirtawahyu.model.Ticket;
import com.tirtawahyu.util.Constants;
import com.tirtawahyu.util.Loading;
import com.tirtawahyu.util.Updateable;
import com.tirtawahyu.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CashierActivity extends AppCompatActivity implements Updateable, Loading {
    @BindView(R.id.ticketCount)
    Spinner spTicketCount;

    @BindView(R.id.addButton)
    Button addButton;

    @BindView(R.id.printButton)
    Button printButton;

    @BindView(R.id.typeOption)
    RadioGroup typeOption;

    @BindView(R.id.ticketList)
    RecyclerView ticketList;

    @BindView(R.id.totalPrice)
    TextView tvTotalPrice;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.tvLoading)
    TextView tvLoading;

    @BindView(R.id.cashier_layout)
    LinearLayout cashierLayout;

    private TicketAdapter ticketAdapter;

    final FirebaseAuth mAuth= FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier);

        ButterKnife.bind(this);
        initComponent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.signOut();
        finish();
    }

    @Override
    public void updateUI() {
        boolean status = ticketAdapter.getItemCount() > 0;
        printButton.setEnabled(status);

        String totalPrice = Util.formatPrice(ticketAdapter.getTotalPrice());
        tvTotalPrice.setText(totalPrice);
    }

    private void initComponent() {
        initAddButton();
        initPrintButton();
        initAdapter();
        initTypeOption();
        initSpinner();
    }

    private void resetComponent() {
        ticketAdapter.setTicketList(new ArrayList<Ticket>());
        ticketAdapter.notifyDataSetChanged();

        String dummyPrice = getResources().getString(R.string.dummy_price);
        tvTotalPrice.setText(dummyPrice);

        initTypeOption();

        spTicketCount.setSelection(0);

        printButton.setEnabled(false);
    }

    private void initAddButton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ticket ticket = createTicket();

                ticketAdapter.addData(ticket);

                updateUI();
            }
        });
    }

    private void initPrintButton() {
        printButton.setEnabled(false);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Receipt receipt = createReceipt();
                showLoading();
                pushToDatabase(receipt);
            }
        });
    }

    private void initAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        ticketList.setLayoutManager(mLayoutManager);

        ticketAdapter = new TicketAdapter(this, new ArrayList<Ticket>());
        ticketList.setAdapter(ticketAdapter);
    }

    private void initTypeOption() {
        typeOption.check(R.id.radioUmum);
    }

    private void initSpinner() {
        List<Integer> spinnerArray =  new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            spinnerArray.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spTicketCount.setAdapter(adapter);
    }

    private Ticket createTicket() {
        int selectedId = typeOption.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);

        String tipe = (String) radioButton.getText();
        int ticketCount = Integer.parseInt(spTicketCount.getSelectedItem().toString());
        int ticketPrice = Constants.TIKET_UMUM;
        int subTotalPrice = ticketCount * ticketPrice;

        Ticket ticket = new Ticket();
        ticket.setTicketId(selectedId);
        ticket.setTipe(tipe);
        ticket.setJumlah(ticketCount);
        ticket.setTotal(subTotalPrice);

        return ticket;
    }

    private Receipt createReceipt() {
        ArrayList<Ticket> tickets = ticketAdapter.getTicketList();

        int umum = 0, member = 0, freePass = 0;
        int total = ticketAdapter.getTotalPrice();
        Date now = new Date();

        for(Ticket t : tickets) {
            int ticketId = t.getTicketId();
            int jumlah = t.getJumlah();

            switch (ticketId) {
                case R.id.radioUmum:
                    umum = jumlah;
                    break;
                case R.id.radioMember:
                    member = jumlah;
                    break;
                case R.id.radioFreePass:
                    freePass = jumlah;
                    break;
            }
        }

        return new Receipt(umum, member, freePass, total, now);
    }

    private void pushToDatabase(Receipt receipt) {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.setFirestoreSettings(settings);

        CollectionReference receiptRef = database.collection("receipt");

        receiptRef.add(receipt).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                resetComponent();
                hideLoading();
            }
        });

        resetComponent();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        cashierLayout.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        tvLoading.setVisibility(View.GONE);
        cashierLayout.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
