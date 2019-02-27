package com.tirtawahyu.db;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class LoginRepository {
    private static LoginRepository INSTANCE;
    private LoginRepositoryCallback<AuthResult> loginCallback;
    private LoginRepositoryCallback<GetTokenResult> tokenCallback;

    public static LoginRepository newInstance() {
        if (INSTANCE == null) {
            synchronized (LoginRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LoginRepository();
                }
            }
        }
        return INSTANCE;
    }

    public void addLoginListener(LoginRepositoryCallback<AuthResult> callback) {
        this.loginCallback = callback;
    }

    public void addTokenListener(LoginRepositoryCallback<GetTokenResult> callback) {
        this.tokenCallback = callback;
    }

    public void attemptLogin(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginCallback.onSuccess(task.getResult());
                } else {
                    loginCallback.onError(task.getException());
                }
            }
        });
    }

    public void getTokenClaims(FirebaseUser user) {
        user.getIdToken(true)
        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    tokenCallback.onSuccess(task.getResult());
                } else {
                    tokenCallback.onError(task.getException());
                }
            }
        });
    }

    public interface LoginRepositoryCallback<T> {
        void onSuccess(T result);

        void onError(Exception e);
    }
}
