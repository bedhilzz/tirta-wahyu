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
import com.tirtawahyu.model.Receipt;
import com.tirtawahyu.model.Ticket;
import com.tirtawahyu.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CashierViewModel extends AndroidViewModel {
    private CashierRepository cashierRepository;

    public MutableLiveData<Boolean> printButtonEnabled;

    public MutableLiveData<String> totalPrice;

    public MutableLiveData<ArrayList<Ticket>> ticketList;

    public MutableLiveData<ArrayList<Item>> itemList;


    public CashierViewModel(@NonNull Application application) {
        super(application);
        cashierRepository = CashierRepository.newInstance();
        printButtonEnabled = new MutableLiveData<>();
        totalPrice = new MutableLiveData<>();
        itemList = new MutableLiveData<>();
        ticketList = new MutableLiveData<>();

        initItems();
        ticketList.setValue(new ArrayList<Ticket>());

        printButtonEnabled.setValue(!ticketList.getValue().isEmpty());
    }

    public Receipt newReceipt() {
        ArrayList<Ticket> tickets = ticketList.getValue();

        int general = 0, member = 0, freePass = 0;
        int total = Util.parsePrice(totalPrice.getValue());
        Date now = new Date();

        for (Ticket t : tickets) {
            String ticketType = t.getTipe();
            int jumlah = t.getJumlah();

            switch (ticketType) {
                case "Umum":
                    general = jumlah;
                    break;
                case "Member":
                    member = jumlah;
                    break;
                default:
                    freePass = jumlah;
                    break;
            }
        }

        return new Receipt(general, member, freePass, total, now);
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
