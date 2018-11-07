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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.tirtawahyu.R;
import com.tirtawahyu.ui.admin.AdminActivity;
import com.tirtawahyu.ui.main.MainActivity;
import com.tirtawahyu.util.Util;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.et_username)
    EditText etUsername;

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.btLogin)
    Button btLogin;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.tvLoading)
    TextView tvLoading;

    @BindView(R.id.login_layout)
    ConstraintLayout loginLayout;

    @BindString(R.string.login_failed_text)
    String loginFailedText;

    @BindString(R.string.login_invalid)
    String loginInfoNotValid;

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
        if (loginLayout.getVisibility() == View.GONE) {
            hideLoading();
        } else {
            super.onBackPressed();
        }
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
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        decideActivityFor(user);
                    } else {
                        Toast.makeText(LoginActivity.this, loginFailedText,
                                Toast.LENGTH_SHORT).show();
                        hideLoading();
                    }
                }
            });
    }

    private void decideActivityFor(FirebaseUser user) {
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    Intent intent;
                    GetTokenResult result = task.getResult();
                    boolean isAdmin = (boolean) result.getClaims().get("admin");

                    if (isAdmin) {
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    String taskFailed = getString(R.string.unknown_failed);
                    Toast.makeText(getApplicationContext(), taskFailed,
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
