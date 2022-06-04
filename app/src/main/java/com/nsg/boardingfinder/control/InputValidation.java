package com.nsg.boardingfinder.control;

import android.app.Activity;
import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class InputValidation {
    private Context context;
    public InputValidation(Context context) {
        this.context = context;
    }

   public boolean isInputEditTextField(EditText editText, String message){
        String value = editText.getText().toString().trim();
        if (value.isEmpty()){
          //  textInputLayout.setError(message);
            hideKeyBoardFrom(editText);
            return false;
        }else{
          //  textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextEmail(EditText email, String message){
        String value = email.getText().toString().trim();
        if (value.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(value).matches()){
           // textInputLayout.setError(message);
            hideKeyBoardFrom(email);
            return false;
        }else{
           // textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

   public boolean isInputEditTextMatches(EditText editText, EditText editText2, String message){
        String value1 = editText2.getText().toString().trim();
        String value2 = editText2.getText().toString().trim();
        if (value1.contentEquals(value2)){
           // textInputLayout.setError(message);
            hideKeyBoardFrom(editText);
            return false;
        }else{
           // textInputLayout.setErrorEnabled(false);
        }
        return true;
    }


    private void hideKeyBoardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
