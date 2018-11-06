package com.tirtawahyu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.tirtawahyu.ui.login.LoginActivity;
import com.tirtawahyu.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAdminActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_admin);
        ButterKnife.bind(this);

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
        String displayName = etDisplayName.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        boolean valid = Util.validate(displayName, username, password);

        if (!valid) {
            String registerInfoNotValid = getString(R.string.register_invalid);
            Toast.makeText(CreateAdminActivity.this, registerInfoNotValid,
                    Toast.LENGTH_SHORT).show();
        } else {
            String email = Util.formatUsername(username);

            attemptRegister(displayName, email, password);
        }
    }

    private void attemptRegister(String displayName, String email, String password) {

    }
}
