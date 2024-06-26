package com.tirtawahyu.views.login;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.tirtawahyu.R;
import com.tirtawahyu.databinding.ActivityLoginBinding;
import com.tirtawahyu.util.Util;
import com.tirtawahyu.viewmodels.login.LoginViewModel;
import com.tirtawahyu.views.cashier.CashierActivity;

import butterknife.BindString;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindString(R.string.login_failed_text)
    String loginFailedText;

    @BindString(R.string.login_invalid)
    String loginInfoNotValid;

    private ActivityLoginBinding binding;

    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.
                setContentView(LoginActivity.this, R.layout.activity_login);

        ButterKnife.bind(this, binding.getRoot());
        initComponent();
    }

    @Override
    public void onBackPressed() {
        if (binding.getIsLoading()) {
            binding.setIsLoading(false);
        } else {
            super.onBackPressed();
        }
    }

    private void initComponent() {
        initLoginButton();
        initViewModel();
    }

    private void initLoginButton() {
        binding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable username = binding.etUsername.getText();
                Editable password = binding.etPassword.getText();

                boolean valid = Util.validate(username.toString(), password.toString());

                if (!valid) {
                    Toast.makeText(LoginActivity.this, loginInfoNotValid,
                            Toast.LENGTH_SHORT).show();
                } else {
                    binding.setIsLoading(true);
                    viewModel.attemptLogin(username.toString(), password.toString());
                }
            }
        });
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        viewModel.getFirebaseUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser firebaseUser) {
                if (firebaseUser == null) {
                    Toast.makeText(LoginActivity.this, loginFailedText,
                            Toast.LENGTH_SHORT).show();
                    binding.setIsLoading(false);
                } else {
                    startCashierActivity();
                }
            }
        });

        binding.setViewmodel(viewModel);

        binding.setIsLoading(false);
    }

    private void startCashierActivity() {
        Intent intent = new Intent(LoginActivity.this, CashierActivity.class);
        startActivity(intent);
        binding.unbind();
        finish();
    }
}
