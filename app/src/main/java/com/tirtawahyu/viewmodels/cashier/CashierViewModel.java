package com.tirtawahyu.viewmodels.cashier;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.tirtawahyu.R;
import com.tirtawahyu.db.CashierRepository;
import com.tirtawahyu.model.Receipt;
import com.tirtawahyu.model.Ticket;
import com.tirtawahyu.util.Util;

import java.util.ArrayList;
import java.util.Date;

public class CashierViewModel extends AndroidViewModel {
    private CashierRepository cashierRepository;

    public MutableLiveData<Boolean> printButtonEnabled;

    public MutableLiveData<String> totalPrice;

    public MutableLiveData<ArrayList<Ticket>> ticketList;

    public MutableLiveData<ArrayList<Integer>> spinnerArray;

    public CashierViewModel(@NonNull Application application) {
        super(application);
        cashierRepository = CashierRepository.newInstance();
        printButtonEnabled = new MutableLiveData<>();
        totalPrice = new MutableLiveData<>();
        ticketList = new MutableLiveData<>();
        spinnerArray = new MutableLiveData<>();

        ticketList.setValue(new ArrayList<Ticket>());

        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            arrayList.add(i);
        }

        spinnerArray.setValue(arrayList);

        printButtonEnabled.setValue(!ticketList.getValue().isEmpty());
    }

    public Receipt newReceipt() {
        ArrayList<Ticket> tickets = ticketList.getValue();

        int general = 0, member = 0, freePass = 0;
        int total = Util.parsePrice(totalPrice.getValue());
        Date now = new Date();

        for (Ticket t : tickets) {
            int ticketId = t.getTicketId();
            int jumlah = t.getJumlah();

            switch (ticketId) {
                case R.id.radioUmum:
                    general = jumlah;
                    break;
                case R.id.radioMember:
                    member = jumlah;
                    break;
                case R.id.radioFreePass:
                    freePass = jumlah;
                    break;
            }
        }

        return new Receipt(general, member, freePass, total, now);
    }

    public Task<DocumentReference> createTransaction() {
        return cashierRepository.createTransaction(newReceipt());
    }
}
