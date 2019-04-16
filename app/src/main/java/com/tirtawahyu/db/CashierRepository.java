package com.tirtawahyu.db;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tirtawahyu.util.Util;

import java.util.Map;

public class CashierRepository {
    private static CashierRepository INSTANCE;
    private FirebaseFirestore database;

    private CashierRepository() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        database = FirebaseFirestore.getInstance();
        database.setFirestoreSettings(settings);
    }

    public static CashierRepository newInstance() {
        if (INSTANCE == null) {
            synchronized (CashierRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CashierRepository();
                }
            }
        }
        return INSTANCE;
    }

    public Task<DocumentReference> createTransaction(Map<String, Object> receipt) {
        CollectionReference receiptRef = database.collection("receipt");

        return receiptRef.add(receipt);
    }

    public Task<QuerySnapshot> getItems() {
        String priceType = "weekday";
        if (Util.isWeekend()) {
            priceType = "weekend";
        }
        Query query = database.
                collection("price").
                whereEqualTo("price_type", priceType).
                orderBy("type", Query.Direction.ASCENDING);

        return query.get();
    }
}
