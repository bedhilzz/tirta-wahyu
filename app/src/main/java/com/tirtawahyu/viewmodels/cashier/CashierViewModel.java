package com.tirtawahyu.viewmodels.cashier;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tirtawahyu.db.CashierRepository;
import com.tirtawahyu.model.Item;
import com.tirtawahyu.model.Ticket;
import com.tirtawahyu.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashierViewModel extends AndroidViewModel {
    private CashierRepository cashierRepository;

    public MutableLiveData<Boolean> printButtonEnabled;

    public MutableLiveData<String> totalPrice;

    public MutableLiveData<Integer> paymentAmount;

    public MutableLiveData<ArrayList<Ticket>> ticketList;

    public MutableLiveData<ArrayList<Item>> itemList;


    public CashierViewModel(@NonNull Application application) {
        super(application);
        cashierRepository = CashierRepository.newInstance();
        printButtonEnabled = new MutableLiveData<>();
        totalPrice = new MutableLiveData<>();
        paymentAmount = new MutableLiveData<>();
        itemList = new MutableLiveData<>();
        ticketList = new MutableLiveData<>();

        initItems();
        ticketList.setValue(new ArrayList<Ticket>());

        printButtonEnabled.setValue(!ticketList.getValue().isEmpty());
    }

    public Map<String, Object> newReceipt() {
        ArrayList<Ticket> tickets = ticketList.getValue();
        Map<String, Object> receipt = new HashMap<>();

        for (Ticket t : tickets) {
            String ticketType = t.getTicketType();
            int amount = t.getQuantity();

            ticketType = ticketType.replace('-', '_');
            ticketType = ticketType.replace(' ', '_');
            ticketType = ticketType.toLowerCase();

            receipt.put(ticketType, amount);
        }

        int total = Util.parsePrice(totalPrice.getValue());
        Date now = new Date();

        receipt.put("total", total);
        receipt.put("created_at", now);

        return receipt;
    }

    public Task<DocumentReference> createTransaction() {
        return cashierRepository.createTransaction(newReceipt());
    }

    public Task<QuerySnapshot> getItems() {
        return cashierRepository.getItems();
    }

    private void initItems() {
        final ArrayList<Item> items = new ArrayList<>();
        getItems().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot item: snapshots) {
                    Item i = new Item(item.getId(), (String) item.get("type"), (int)((long) item.get("price")));
                    items.add(i);
                }
                itemList.setValue(items);
            }
        });
    }
}
