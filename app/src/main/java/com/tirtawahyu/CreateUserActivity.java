package com.tirtawahyu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.tirtawahyu.util.Loading;
import com.tirtawahyu.util.Util;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateUserActivity extends AppCompatActivity implements Loading {
    @BindView(R.id.et_display_name)
    EditText etDisplayName;

    @BindView(R.id.et_username)
    EditText etUsername;

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.bt_register)
    Button btRegister;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.register_layout)
    ConstraintLayout registerLayout;

    @BindView(R.id.tvLoading)
    TextView tvLoading;

    final FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        ButterKnife.bind(this);

        setTitle(R.string.create_user_activity);
        initComponent();
    }

    private void initComponent() {
        initRegisterButton();
        initRadioButton();
    }

    private void initRegisterButton() {
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
    }

    private void initRadioButton() {
        radioGroup.check(R.id.radio_administrator);
    }

    private void createUser() {
        int selectedId = radioGroup.getCheckedRadioButtonId();

        String displayName = etDisplayName.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        boolean isAdmin = selectedId == R.id.radio_administrator;

        boolean valid = Util.validate(displayName, username, password);

        if (!valid) {
            String registerInfoNotValid = getString(R.string.register_invalid);
            Toast.makeText(CreateUserActivity.this, registerInfoNotValid,
                    Toast.LENGTH_SHORT).show();
        } else {
            String email = Util.formatUsername(username);

            showLoading();
            attemptRegister(displayName, email, password, isAdmin);
        }
    }

    private void attemptRegister(final String displayName, String email, String password, final boolean isAdmin) {
        Map<String, Object> data = new HashMap<>();
        data.put("displayName", displayName);
        data.put("email", email);
        data.put("password", password);
        data.put("isAdmin", isAdmin);

        mFunctions.getHttpsCallable("register").call(data)
            .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                @Override
                public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                    if (task.isSuccessful()) {
                        String createUserSuccess = getString(R.string.register_success);
                        Toast.makeText(CreateUserActivity.this, createUserSuccess,
                                Toast.LENGTH_SHORT).show();

                        finish();
                    } else {
                        String error = getString(R.string.unknown_failed);
                        Toast.makeText(CreateUserActivity.this, error,
                                Toast.LENGTH_SHORT).show();
                    }
                    hideLoading();
                }
            });
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        registerLayout.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        tvLoading.setVisibility(View.GONE);
        registerLayout.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
