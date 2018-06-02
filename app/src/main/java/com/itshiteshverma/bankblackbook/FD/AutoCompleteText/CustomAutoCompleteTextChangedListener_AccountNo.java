package com.itshiteshverma.bankblackbook.FD.AutoCompleteText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.itshiteshverma.bankblackbook.FD.AddFD;


public class CustomAutoCompleteTextChangedListener_AccountNo implements TextWatcher {

    public static final String TAG = "CustomAutoCompleteTextChangedListener_BankName.java";
    Context context;

    public CustomAutoCompleteTextChangedListener_AccountNo(Context context) {
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        // if you want to see in the logcat what the user types
        Log.e(TAG, "User input: " + userInput);

        AddFD mainActivity = ((AddFD) context);

        // query the database based on the user input
        mainActivity.item = mainActivity.get_AccountNo_FromDb(userInput.toString());

        // update the adapater
        mainActivity.myAdapter_AccountNo.notifyDataSetChanged();
        mainActivity.myAdapter_AccountNo = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, mainActivity.item);
        mainActivity.AccountNumber.setAdapter(mainActivity.myAdapter_AccountNo);

    }

}