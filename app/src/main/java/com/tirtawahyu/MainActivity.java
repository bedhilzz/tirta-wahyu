package com.tirtawahyu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                String ticketCount = etTicketCount.getText().toString();
            }
        });
    }

    private void initAdapter() {
        ticketAdapter = new TicketAdapter(new ArrayList<Ticket>());
    }
}
