package com.tirtawahyu.db;

import android.app.Application;

public class CashierRepository {
    private static CashierRepository INSTANCE;

    private CashierRepository(Application application) {
    }

    public static CashierRepository newInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (CashierRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CashierRepository(application);
                }
            }
        }
        return INSTANCE;
    }
}
