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
import com.wolff.wtracker.tools.OtherTools;

import static com.wolff.wtracker.tools.PreferencesTools.IS_DEBUG;

/**
 * Created by wolff on 05.10.2017.
 */

public class Register_user_fragment extends Fragment {
    private EditText edUserName;
    private EditText edUserPassword;
    private EditText edUserPhone;
    private EditText edUserIMEI;
    private EditText edUserPin;

    private Button btnRegister;
    private Button btnLogin;
    private Register_user_fragment_listener listener;

    public interface Register_user_fragment_listener {
        void onClickButtonRegisterLoginUser(String buttonType, WUser user);
    }

    public static Register_user_fragment newInstance() {
        Register_user_fragment fragment = new Register_user_fragment();
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
        View view = inflater.inflate(R.layout.register_user_fragment, container, false);

        edUserName = (EditText) view.findViewById(R.id.edUserName);
        edUserPassword = (EditText) view.findViewById(R.id.edUserPassword);
        edUserPhone = (EditText) view.findViewById(R.id.edUserPhone);
        edUserIMEI = (EditText) view.findViewById(R.id.edUserIMEI);
        edUserPin = (EditText) view.findViewById(R.id.edUserPin);

        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnRegister = (Button) view.findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(btnLoginOnClickListener);
        btnRegister.setOnClickListener(btnRegisterOnClickListener);

        edUserName.addTextChangedListener(textWatcher);
        edUserPassword.addTextChangedListener(textWatcher);
        edUserPhone.addTextChangedListener(textWatcher);
        edUserPin.addTextChangedListener(textWatcher);
        edUserIMEI.setText(new OtherTools().getIMEI(getContext()));
        if (IS_DEBUG) {
            edUserName.setText("Wolfff");
            edUserPhone.setText("380673231646");
            edUserPassword.setText("777555");
            edUserPin.setText("1234");
        }
        setViewEnabled();
        return view;
    }

    private void setViewEnabled() {
        boolean isEnabled = (edUserName.getText().toString().length() > 3) &&
                (edUserPassword.getText().toString().length() > 3) &&
                (edUserPhone.getText().toString().length() == 12) &&
                (edUserPin.getText().toString().length() > 3);
        btnRegister.setEnabled(isEnabled);
        btnLogin.setEnabled(isEnabled);
    }

    View.OnClickListener btnLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listener.onClickButtonRegisterLoginUser("LOGIN", getUserFromRegForm());
        }
    };
    View.OnClickListener btnRegisterOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnRegister.setEnabled(false);
            btnLogin.setEnabled(false);


            listener.onClickButtonRegisterLoginUser("REGISTER", getUserFromRegForm());

            btnRegister.setEnabled(true);
            btnLogin.setEnabled(true);

        }
    };

    private WUser getUserFromRegForm() {
        WUser newUser = new WUser();
        newUser.set_currentUser(true);
        newUser.set_phone(edUserPhone.getText().toString());
        newUser.set_password(edUserPassword.getText().toString());
        newUser.set_name(edUserName.getText().toString());
        newUser.set_imei_phone(edUserIMEI.getText().toString());
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
        listener = (Register_user_fragment_listener) context;
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
