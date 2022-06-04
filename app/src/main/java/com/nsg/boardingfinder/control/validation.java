package com.nsg.boardingfinder.control;

import android.content.Context;

import com.google.android.material.textfield.TextInputLayout;

public class validation {

    private Context context;
    validation(Context context) {
        this.context = context;
    }

    private TextInputLayout name;
    private TextInputLayout email;
    private TextInputLayout phone;
    private TextInputLayout address;
    private TextInputLayout password;
    private TextInputLayout rePassword;

}
