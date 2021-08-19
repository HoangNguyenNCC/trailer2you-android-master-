package com.applutions.t2y.common;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

public class ExpiryDateWatcher implements TextWatcher {

    public TextInputEditText edit_text;

    public ExpiryDateWatcher(TextInputEditText edit_text) {
        this.edit_text = edit_text;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if (start == 1 && start+count == 2 && !s.toString().contains("/")) {
//            edit_text.setText(s.toString() + "/");
//        } else if (start == 3 && start-before == 2 && s.toString().contains("/")) {
//            edit_text.setText(s.toString().replace("/", ""));
//        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() > 0 && (editable.length() % 3) == 0) {
            final char c = editable.charAt(editable.length() - 1);
            if ('/' == c) {
                editable.delete(editable.length() - 1, editable.length());
            }
        }
        if (editable.length() > 0 && (editable.length() % 3) == 0) {
            char c = editable.charAt(editable.length() - 1);
            if (Character.isDigit(c) && TextUtils.split(editable.toString(), "/").length <= 2) {
                editable.insert(editable.length() - 1, "/");
            }
        }

    }
}
