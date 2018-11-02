package com.tirtawahyu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements Updateable {
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

    private TicketAdapter ticketAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        initComponent();
    }

    private void initComponent() {
        initAddButton();
        initAdapter();
        initTypeOption();
        initSpinner();
    }

    private void initAddButton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = typeOption.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);

                String tipe = (String) radioButton.getText();
                int ticketCount = Integer.parseInt(spTicketCount.getSelectedItem().toString());
                int ticketPrice = Constants.TIKET_UMUM;
                int subTotalPrice = ticketCount * ticketPrice;

                Ticket ticket = new Ticket();
                ticket.setTicketId(selectedId);
                ticket.setTipe(tipe);
                ticket.setJumlah(ticketCount);
                ticket.setTotal(subTotalPrice);

                ticketAdapter.addData(ticket);

                updateUI();
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

    @Override
    public void updateUI() {
        String totalPrice = Util.formatPrice(ticketAdapter.getTotalPrice());
        tvTotalPrice.setText(totalPrice);
    }
}
