package com.tirtawahyu.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.tirtawahyu.R;
import com.tirtawahyu.ui.admin.AdminActivity;
import com.tirtawahyu.util.Util;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.etUsername)
    EditText etUsername;

    @BindView(R.id.etPassword)
    EditText etPassword;

    @BindView(R.id.btLogin)
    Button btLogin;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.tvLoading)
    TextView tvLoading;

    @BindView(R.id.loginLayout)
    ConstraintLayout loginLayout;

    @BindString(R.string.login_failed_text)
    String loginFailedText;

    @BindString(R.string.login_invalid)
    String loginInfoNotValid;

    final FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    final FirebaseAuth mAuth= FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        initComponent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideLoading();
    }

    private void initComponent() {
        initLoginButton();
    }

    private void initLoginButton() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                boolean valid = Util.validate(username, password);

                if (!valid) {
                    Toast.makeText(LoginActivity.this, loginInfoNotValid,
                            Toast.LENGTH_SHORT).show();
                } else {
                    String email = Util.formatUsername(username);

                    showLoading();
                    attemptLogin(email, password);
                }
            }
        });
    }

    private void attemptLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, loginFailedText,
                                Toast.LENGTH_SHORT).show();
                    }
                    hideLoading();
                }
            });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        tvLoading.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
