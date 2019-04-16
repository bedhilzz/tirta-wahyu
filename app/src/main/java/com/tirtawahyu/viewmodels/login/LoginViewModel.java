package com.tirtawahyu.viewmodels.login;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.tirtawahyu.db.LoginRepository;
import com.tirtawahyu.util.Util;

public class LoginViewModel extends AndroidViewModel {
    private LoginRepository loginRepository;

    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> firebaseUser;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        loginRepository = LoginRepository.newInstance();
    }


    public LiveData<FirebaseUser> getFirebaseUser() {
        if (firebaseUser == null) {
            firebaseUser = new MutableLiveData<>();
        }
        return firebaseUser;
    }

    public void attemptLogin(String username, String password) {
        String email = Util.formatUsername(username);
        loginRepository.addLoginListener(new LoginRepository.LoginRepositoryCallback<AuthResult>() {
            @Override
            public void onSuccess(AuthResult result) {
                firebaseUser.setValue(result.getUser());
            }

            @Override
            public void onError(Exception e) {
                firebaseUser.setValue(null);
            }
        });
        loginRepository.attemptLogin(email, password);
    }
}
