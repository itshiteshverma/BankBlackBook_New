package com.itshiteshverma.bankblackbook;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wilmar Africa Ltd on 18-06-17.
 */

class OneFragment_GetterSetter {
    private String policy_name;
    private String policy_number;
    private String bank_name;
    private String due_date;
    private String type;
    private String maturity_date;

    public OneFragment_GetterSetter() {
    }

    public OneFragment_GetterSetter(String policy_name, String policy_number, String bank_name, String due_date, String type) {
        this.policy_name = policy_name;
        this.policy_number = policy_number;
        this.bank_name = bank_name;
        this.due_date = due_date;
        this.type = type;
    }


    public String getDue_date() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String EditedDueDate = null;
        Date date3;
        try {
            if (type.equals("FD")) {
                date3 = formatter.parse(due_date);
                SimpleDateFormat newFormat2 = new SimpleDateFormat("dd MMM yy");
                EditedDueDate = newFormat2.format(date3);
            } else {
                date3 = formatter.parse("2017-" + due_date);
                SimpleDateFormat newFormat2 = new SimpleDateFormat("dd MMM");
                EditedDueDate = newFormat2.format(date3);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return EditedDueDate;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getPolicy_name() {
        return policy_name;
    }

    public void setPolicy_name(String policy_name) {
        this.policy_name = policy_name;
    }

    public String getPolicy_number() {
        return policy_number;
    }

    public void setPolicy_number(String policy_number) {
        this.policy_number = policy_number;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMaturity_date() {
        return maturity_date;
    }

    public void setMaturity_date(String maturity_date) {
        this.maturity_date = maturity_date;
    }
}
