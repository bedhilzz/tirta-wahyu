package com.tirtawahyu;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserManagementFragment extends Fragment {
    @BindView(R.id.fab_create_user)
    FloatingActionButton fabCreateUser;

    public UserManagementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_user_management, container, false);
        ButterKnife.bind(this, view);

        initComponent();
        return view;
    }

    private void initComponent() {
        initFloatingButton();
    }

    private void initFloatingButton() {
        fabCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateUserActivity.class);
                startActivity(intent);
            }
        });
    }
}
