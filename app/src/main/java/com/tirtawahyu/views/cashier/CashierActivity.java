package com.tirtawahyu.views.cashier;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.tirtawahyu.R;
import com.tirtawahyu.databinding.ActivityCashierBinding;
import com.tirtawahyu.model.Receipt;
import com.tirtawahyu.model.Ticket;
import com.tirtawahyu.util.Constants;
import com.tirtawahyu.util.Loading;
import com.tirtawahyu.util.Updateable;
import com.tirtawahyu.util.Util;
import com.tirtawahyu.viewmodels.cashier.CashierViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CashierActivity extends AppCompatActivity implements Updateable, OnCompleteListener<DocumentReference> {
    private TicketAdapter ticketAdapter;

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    boolean doubleBackToExitPressedOnce = false;

    private ActivityCashierBinding binding;

    private CashierViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cashier);

        initComponent();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            mAuth.signOut();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.double_back_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void updateUI() {
        boolean status = !viewModel.ticketList.getValue().isEmpty();
        viewModel.printButtonEnabled.setValue(status);

        String totalPrice = Util.formatPrice(ticketAdapter.getTotalPrice());
        viewModel.totalPrice.setValue(totalPrice);
    }

    @Override
    public void onComplete(@NonNull Task<DocumentReference> task) {
        binding.setIsLoading(false);
        if (task.isSuccessful()) {
            resetComponent();
        }
    }

    private void initComponent() {
        initViewModel();
        initAddButton();
        initPrintButton();
        initAdapter();
        initTypeOption();
        initSpinner();
    }

    private void resetComponent() {
        String dummyPrice = getResources().getString(R.string.dummy_price);
        viewModel.totalPrice.setValue(dummyPrice);
        viewModel.ticketList.setValue(new ArrayList<Ticket>());
        viewModel.printButtonEnabled.setValue(false);

        ticketAdapter.setTicketList(viewModel.ticketList.getValue());
        ticketAdapter.notifyDataSetChanged();

        initTypeOption();

        binding.ticketCount.setSelection(0);

    }

    private void initAddButton() {
        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ticket ticket = createTicket();

                ticketAdapter.addData(ticket);

                viewModel.ticketList.setValue(ticketAdapter.getTicketList());

                updateUI();
            }
        });
    }

    private void initPrintButton() {
        boolean status = !viewModel.ticketList.getValue().isEmpty();
        viewModel.printButtonEnabled.setValue(status);
        binding.printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.setIsLoading(true);
                createTransaction();
            }
        });
    }

    private void initAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.ticketList.setLayoutManager(mLayoutManager);

        ticketAdapter = new TicketAdapter(this, viewModel.ticketList.getValue());
        binding.ticketList.setAdapter(ticketAdapter);
    }

    private void initTypeOption() {
        binding.typeOption.check(R.id.radioUmum);
    }

    private void initSpinner() {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, viewModel.spinnerArray.getValue());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.ticketCount.setAdapter(adapter);
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(CashierViewModel.class);
        binding.setIsLoading(false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
    }

    private Ticket createTicket() {
        int selectedId = binding.typeOption.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);

        String tipe = (String) radioButton.getText();
        int ticketCount = Integer.parseInt(binding.ticketCount.getSelectedItem().toString());
        int ticketPrice = Util.priceTicketWith(selectedId);
        int subTotalPrice = ticketCount * ticketPrice;

        return new Ticket(
                selectedId,
                tipe,
                ticketCount,
                subTotalPrice
        );
    }

    private void createTransaction() {
        viewModel.createTransaction().addOnCompleteListener(this);
    }
}
