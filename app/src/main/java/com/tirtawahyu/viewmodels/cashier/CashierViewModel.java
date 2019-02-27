package com.tirtawahyu.viewmodels.cashier;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.tirtawahyu.db.CashierRepository;

public class CashierViewModel extends AndroidViewModel {
    private CashierRepository historyRepository;

    public CashierViewModel(@NonNull Application application, int uid) {
        super(application);
        historyRepository = CashierRepository.newInstance(application);
    }
}
