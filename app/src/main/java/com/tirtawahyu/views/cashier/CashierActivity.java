package com.tirtawahyu.views.cashier;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.tirtawahyu.util.bluetooth.BluetoothService;
import com.tirtawahyu.util.printer.Command;
import com.tirtawahyu.util.printer.PrinterCommand;
import com.tirtawahyu.util.Updateable;
import com.tirtawahyu.util.Util;
import com.tirtawahyu.viewmodels.cashier.CashierViewModel;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import static com.tirtawahyu.util.Constants.DEVICE_NAME;
import static com.tirtawahyu.util.Constants.MESSAGE_CONNECTION_LOST;
import static com.tirtawahyu.util.Constants.MESSAGE_DEVICE_NAME;
import static com.tirtawahyu.util.Constants.MESSAGE_READ;
import static com.tirtawahyu.util.Constants.MESSAGE_STATE_CHANGE;
import static com.tirtawahyu.util.Constants.MESSAGE_TOAST;
import static com.tirtawahyu.util.Constants.MESSAGE_UNABLE_CONNECT;
import static com.tirtawahyu.util.Constants.MESSAGE_WRITE;
import static com.tirtawahyu.util.Constants.TOAST;

public class CashierActivity extends AppCompatActivity implements Updateable, OnCompleteListener<DocumentReference>, ItemAdapter.OnAddItemClickListener {
    private TicketAdapter ticketAdapter;

    private ItemAdapter itemAdapter;

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    boolean doubleBackToExitPressedOnce = false;

    private ActivityCashierBinding binding;

    private CashierViewModel viewModel;

    private BluetoothService mService;

    private BluetoothAdapter mBluetoothAdapter;

    private String mConnectedDeviceName = null;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cashier);

        boolean vertical = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        binding.setIsVertical(vertical);

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
    protected void onResume() {
        super.onResume();
        if (mService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
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
            printReceipt();
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

    private void initComponent() {
        initViewModel();
        initBluetoothAdapter();
        initBluetoothHandler();
        initBluetoothService();
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
        mBluetoothAdapter.enable();
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

    @SuppressLint("HandlerLeak")
    private void initBluetoothHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = "";
                switch (msg.what) {
                    case MESSAGE_WRITE:
                        message = "Mencetak Struk...";
                        break;
                    case MESSAGE_DEVICE_NAME:
                        mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                        message = "Connected to " + mConnectedDeviceName;
                        break;
                    case MESSAGE_TOAST:
                        message = msg.getData().getString(TOAST);
                        break;
                    case MESSAGE_CONNECTION_LOST:
                        message = "Device connection was lost";
                        break;
                    case MESSAGE_UNABLE_CONNECT:
                        message =  "Unable to connect device";
                        break;
                }
                showToast(message);
            }
        };
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_SHORT)
                .show();
    }

    private Ticket createTicket(Item i) {
        String tipe = i.getTipe();
        int ticketCount = 1;
        int ticketPrice = i.getPrice();
        int subTotalPrice = ticketCount * ticketPrice;

        return new Ticket(
                i.getItemId(),
                tipe,
                ticketCount,
                subTotalPrice
        );
    }

    private void createTransaction() {
        if (mService.getState() == BluetoothService.STATE_CONNECTED) {
            viewModel.createTransaction().addOnCompleteListener(this);
        } else {
            showToast(getString(R.string.not_connected));
        }
    }

    private void SendDataByte(byte[] data) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            showToast(getString(R.string.not_connected));
            return;
        }
        mService.write(data);
    }

    private void printReceipt() {
        try {
            Command.ESC_Align[2] = 0x01;
            SendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x11;
            SendDataByte(Command.GS_ExclamationMark);
            SendDataByte("WAHYU TIRTA ADI\n".getBytes("GBK"));


            Command.GS_ExclamationMark[2] = 0x00;
            SendDataByte(Command.GS_ExclamationMark);

            SendDataByte("--------------------------------\n".getBytes("GBK"));
            String now = Util.formatDate(new Date().getTime(), "d MMMM yyyy HH:mm:ss");
            SendDataByte(String.format("%s%n", now).getBytes("GBK"));
            SendDataByte("--------------------------------\n".getBytes("GBK"));

            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);

            int total = 0;
            for (Ticket t : viewModel.ticketList.getValue()) {
                total += t.getTotal();
                int price = t.getTotal() / t.getJumlah();
                String ticket = String.format("%-13s  %2s  %5s  %6s%n", t.getTipe(), t.getJumlah(), price, t.getTotal());
                SendDataByte(ticket.getBytes("GBK"));
            }

            SendDataByte("--------------------------------\n".getBytes("GBK"));

            Command.ESC_Align[2] = 0x02;
            SendDataByte(Command.ESC_Align);

            String totalPrice = String.format("%s  %s%n%n", "TOTAL:", total);
            SendDataByte(totalPrice.getBytes("GBK"));

            SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(48));
            SendDataByte(Command.GS_V_m_n);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
