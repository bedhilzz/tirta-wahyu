package com.tirtawahyu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.ticketCount)
    EditText etTicketCount;

    @BindView(R.id.addButton)
    Button addButton;

    @BindView(R.id.printButton)
    Button printButton;

    @BindView(R.id.typeOption)
    RadioGroup typeOption;

    @BindView(R.id.ticketList)
    RecyclerView ticketList;

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
    }

    private void initAddButton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = typeOption.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);

                String tipe = (String) radioButton.getText();
                int ticketCount = Integer.parseInt(etTicketCount.getText().toString());
                int ticketPrice = 4000;

                int totalPrice = ticketCount * ticketPrice;

                Ticket ticket = new Ticket();
                ticket.setTipe(tipe);
                ticket.setJumlah(ticketCount);
                ticket.setTotal(totalPrice);

                ticketAdapter.addData(ticket);
                ticketAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        ticketList.setLayoutManager(mLayoutManager);

        ticketAdapter = new TicketAdapter(this, new ArrayList<Ticket>());
        ticketList.setAdapter(ticketAdapter);
    }
}
