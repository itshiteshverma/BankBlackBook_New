package com.itshiteshverma.bankblackbook.Policy.AutoCompleteText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.itshiteshverma.bankblackbook.Policy.AddPolicy;


public class CustomAutoCompleteTextChangedListener_PolicyType implements TextWatcher {

    public static final String TAG = "CustomAutoCompleteTextChangedListener_BankName.java";
    Context context;

    public CustomAutoCompleteTextChangedListener_PolicyType(Context context) {
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

        AddPolicy mainActivity = ((AddPolicy) context);

        // query the database based on the user input
        mainActivity.item = mainActivity.get_PolicyType_FromDb(userInput.toString());

        // update the adapater
        mainActivity.myAdapter_PolciyType.notifyDataSetChanged();
        mainActivity.myAdapter_PolciyType = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, mainActivity.item);
        mainActivity.PolicyType.setAdapter(mainActivity.myAdapter_PolciyType);

    }

}