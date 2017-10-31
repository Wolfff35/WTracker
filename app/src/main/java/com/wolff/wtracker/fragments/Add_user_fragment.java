package com.wolff.wtracker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wolff.wtracker.R;
import com.wolff.wtracker.model.WUser;

import static com.wolff.wtracker.tools.PreferencesTools.IS_DEBUG;

/**
 * Created by wolff on 05.10.2017.
 */

public class Add_user_fragment extends Fragment {
    private EditText edUserName;
    private EditText edUserPhone;
    private EditText edUserPin;

    private Button btnAddUser;
    private Add_user_fragment_listener listener;

    public interface Add_user_fragment_listener {
        void onClickButtonAddUser(WUser user);
    }

    public static Add_user_fragment newInstance() {
        Add_user_fragment fragment = new Add_user_fragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_user_fragment, container, false);

        edUserName = (EditText) view.findViewById(R.id.edUserName);
        edUserPhone = (EditText) view.findViewById(R.id.edUserPhone);
        edUserPin = (EditText) view.findViewById(R.id.edUserPin);

        btnAddUser = (Button) view.findViewById(R.id.btnAddUser);

        btnAddUser.setOnClickListener(btnAddUserOnClickListener);

        edUserName.addTextChangedListener(textWatcher);
        edUserPhone.addTextChangedListener(textWatcher);
        edUserPin.addTextChangedListener(textWatcher);
        if (IS_DEBUG) {
            edUserName.setText("Wolfff");
            edUserPhone.setText("380673231646");
            edUserPin.setText("1234");
        }
        setViewEnabled();
        return view;
    }

    private void setViewEnabled() {
        boolean isEnabled = (edUserName.getText().toString().length() > 3) &&
                (edUserPhone.getText().toString().length() == 12) &&
                (edUserPin.getText().toString().length() > 3);
        btnAddUser.setEnabled(isEnabled);
    }

    View.OnClickListener btnAddUserOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnAddUser.setEnabled(false);


            listener.onClickButtonAddUser(getUserFromRegForm());

            btnAddUser.setEnabled(true);

        }
    };

    private WUser getUserFromRegForm() {
        WUser newUser = new WUser();
        newUser.set_currentUser(false);
        newUser.set_phone(edUserPhone.getText().toString());
        newUser.set_name(edUserName.getText().toString());
        //newUser.set_avatar_path();
        newUser.set_pin_for_access(edUserPin.getText().toString());
        newUser.set_id_user(edUserPhone.getText().toString());
        return newUser;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (Add_user_fragment_listener) context;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setViewEnabled();
        }
    };
}
