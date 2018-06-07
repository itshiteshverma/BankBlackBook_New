package com.itshiteshverma.bankblackbook.Policy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wilmar Africa Ltd on 18-06-17.
 */

class PolicyPremium_GetterSetter {

    private String amount_paid;
    private String mode_of_payment;
    private String premium_date;
    private String recipt_no;
    private String remarks;
//    private String timestamp;


    public PolicyPremium_GetterSetter() {
    }

    public PolicyPremium_GetterSetter(String recipt_no, String premium_date, String amount_paid,
                                      String remarks, String mode_of_payment) {
        this.amount_paid = amount_paid;
        this.mode_of_payment = mode_of_payment;
        this.premium_date = premium_date;
        this.recipt_no = recipt_no;
        this.remarks = remarks;
//        this.timestamp = timestamp;

    }

    //
    public String getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(String amount_paid) {
        this.amount_paid = amount_paid;
    }

    //
    public String getMode_of_payment() {
        return mode_of_payment;
    }

    public void setMode_of_payment(String mode_of_payment) {
        this.mode_of_payment = mode_of_payment;
    }

    //
    public String getPremium_date() {
        String EditedPremiumDate = "00-00-00";
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(premium_date);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy");
            EditedPremiumDate = newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return EditedPremiumDate;
    }

    public void setPremium_date(String premium_date) {
        this.premium_date = premium_date;
    }

    public String getRecipt_no() {
        return recipt_no;
    }

    public void setRecipt_no(String recipt_no) {
        this.recipt_no = recipt_no;
    }

    //
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

//
//    public String getTimestamp() {
//        DateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
//        Date date1 = null;
//        try {
//            date1 = formatter.parse(timestamp);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        SimpleDateFormat newFormat2 = new SimpleDateFormat("dd MMM");
//        String EditedStartDate = newFormat2.format(date1);
//        return EditedStartDate;
//    }
//
//    public void setTimestamp(String timestamp) {
//
//        this.timestamp = timestamp;
//    }


}
