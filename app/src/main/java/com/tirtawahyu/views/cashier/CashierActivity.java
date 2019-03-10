package com.tirtawahyu.views.cashier;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.tirtawahyu.R;
import com.tirtawahyu.databinding.ActivityCashierBinding;
import com.tirtawahyu.model.Item;
import com.tirtawahyu.model.Ticket;
import com.tirtawahyu.util.Constants;
import com.tirtawahyu.util.Updateable;
import com.tirtawahyu.util.Util;
import com.tirtawahyu.util.bluetooth.BluetoothService;
import com.tirtawahyu.util.printer.PrinterController;
import com.tirtawahyu.viewmodels.cashier.CashierViewModel;

import java.util.ArrayList;

import static com.tirtawahyu.util.Constants.MESSAGE_CONNECTION_LOST;
import static com.tirtawahyu.util.Constants.MESSAGE_DEVICE_NAME;
import static com.tirtawahyu.util.Constants.MESSAGE_STATE_CHANGE;
import static com.tirtawahyu.util.Constants.MESSAGE_TOAST;
import static com.tirtawahyu.util.Constants.MESSAGE_UNABLE_CONNECT;
import static com.tirtawahyu.util.Constants.MESSAGE_WRITE;
import static com.tirtawahyu.util.Constants.TOAST;
import static com.tirtawahyu.util.bluetooth.BluetoothService.STATE_CONNECTED;
import static com.tirtawahyu.util.bluetooth.BluetoothService.STATE_CONNECTING;
import static com.tirtawahyu.util.bluetooth.BluetoothService.STATE_NONE;

public class CashierActivity extends AppCompatActivity implements Updateable, OnCompleteListener<DocumentReference>, ItemAdapter.OnAddItemClickListener {
    private static final int REQUEST_ENABLE_BT = 2;

    private TicketAdapter ticketAdapter;
    private ItemAdapter itemAdapter;

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    boolean doubleBackToExitPressedOnce = false;

    private ActivityCashierBinding binding;

    private CashierViewModel viewModel;

    private BluetoothService mService;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    private String mConnectedDeviceName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cashier);

        boolean vertical = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        binding.setIsVertical(vertical);

        initBluetoothHandler();
        initBluetoothAdapter();
        initComponent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mService == null) {
                initBluetoothService();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            mAuth.signOut();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        showSnackBar(getString(R.string.double_back_to_exit));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mService != null) {
            if (mService.getState() == STATE_NONE) {
                mService.start();
            }
        }
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
            sendDataByte(PrinterController.getFormattedReceipt(viewModel.ticketList.getValue()));
            resetComponent();
        }
    }

    @Override
    public void onItemAddClick(Item i) {
        Ticket ticket = createTicket(i);

        ticketAdapter.addData(ticket);

        viewModel.ticketList.setValue(ticketAdapter.getTicketList());

        updateUI();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        boolean vertical = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        binding.setIsVertical(vertical);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:{
                if (resultCode == Activity.RESULT_OK) {
                    initBluetoothService();
                }
                break;
            }
        }
    }

    private void initComponent() {
        initViewModel();
        initPrintButton();
        initAdapter();
    }

    private void resetComponent() {
        String dummyPrice = getResources().getString(R.string.dummy_price);
        viewModel.totalPrice.setValue(dummyPrice);
        viewModel.ticketList.setValue(new ArrayList<Ticket>());
        viewModel.printButtonEnabled.setValue(false);

        ticketAdapter.setTicketList(viewModel.ticketList.getValue());
        ticketAdapter.notifyDataSetChanged();

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
        binding.itemList.setLayoutManager(new LinearLayoutManager(this));
        binding.ticketList.setLayoutManager(new LinearLayoutManager(this));

        ticketAdapter = new TicketAdapter(this, viewModel.ticketList.getValue());
        binding.ticketList.setAdapter(ticketAdapter);
    }

    private void initItemAdapter(ArrayList<Item> items) {
        itemAdapter = new ItemAdapter(this, items);
        binding.itemList.setAdapter(itemAdapter);
        binding.setIsLoading(false);
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(CashierViewModel.class);
        binding.setIsLoading(true);
        viewModel.itemList.observe(this, new Observer<ArrayList<Item>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Item> items) {
                initItemAdapter(items);
            }
        });
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
    }

    private void initBluetoothAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showSnackBar(getString(R.string.bluetooth_not_available));
            finish();
        }
    }

    @SuppressLint("HandlerLeak")
    private void initBluetoothHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case STATE_CONNECTED:
                                showSnackBar(getString(R.string.bluetooth_connected, mConnectedDeviceName));
                                break;
                            case STATE_CONNECTING:
                                showSnackBar(getString(R.string.bluetooth_connecting));
                                break;
                            case STATE_NONE:
                                showSnackBar(getString(R.string.bluetooth_not_connected));
                                break;
                        }
                        break;
                    case MESSAGE_WRITE:
                        showToast(getString(R.string.bluetooth_writing));
                        break;
                    case MESSAGE_DEVICE_NAME:
                        mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                        showSnackBar(getString(R.string.bluetooth_connected, mConnectedDeviceName));
                        break;
                    case MESSAGE_TOAST:
                        showSnackBar(msg.getData().getString(TOAST));
                        break;
                    case MESSAGE_CONNECTION_LOST:
                        showSnackBar(getString(R.string.bluetooth_connection_lost));
                        break;
                    case MESSAGE_UNABLE_CONNECT:
                        showSnackBar(getString(R.string.bluetooth_unable_connect));
                        break;
                }
            }
        };
    }

    private void initBluetoothService() {
        mService = new BluetoothService(this, mHandler);
        String address = Constants.PRINTER_MAC_ADDRESS;

        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            BluetoothDevice device = mBluetoothAdapter
                    .getRemoteDevice(address);
            mService.connect(device);
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_SHORT)
                .show();
    }

    private Ticket createTicket(Item i) {
        String ticketType = i.getTicketType();
        int ticketCount = 1;
        int ticketPrice = i.getPrice();
        int subTotalPrice = ticketCount * ticketPrice;

        return new Ticket(
                i.getItemId(),
                ticketType,
                ticketCount,
                subTotalPrice
        );
    }

    private void createTransaction() {
        if (mService.getState() == STATE_CONNECTED) {
            viewModel.createTransaction().addOnCompleteListener(this);
        } else {
            showSnackBar(getString(R.string.bluetooth_not_connected));
            binding.setIsLoading(false);
        }
    }

    private void sendDataByte(byte[] data) {
        if (mService.getState() != STATE_CONNECTED) {
            showSnackBar(getString(R.string.bluetooth_not_connected));
            return;
        }
        mService.write(data);
    }
}
