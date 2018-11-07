package com.tirtawahyu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.functions.FirebaseFunctions;
import com.tirtawahyu.util.Util;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateUserActivity extends AppCompatActivity {
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

    final FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
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
            attemptRegister(displayName, email, password, isAdmin);
        }
    }

    private void attemptRegister(final String displayName, String email, String password, final boolean isAdmin) {
        mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            boolean setClaimsSuccess = setClaims(isAdmin);
                            if (setClaimsSuccess) {
                                setDisplayNameFor(user, displayName);
                            }
                        } else {
                            String error = getString(R.string.unknown_failed);
                            Toast.makeText(CreateUserActivity.this, error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    
    private boolean setDisplayNameFor(FirebaseUser user, String displayName) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();
        
        return user.updateProfile(request).isSuccessful();
    }

    private boolean setClaims(boolean isAdmin) {
        Map<String, Boolean> data = new HashMap<>();
        data.put("isAdmin", isAdmin);

        return mFunctions.getHttpsCallable("setClaims").call(data).isSuccessful();
    }
}
