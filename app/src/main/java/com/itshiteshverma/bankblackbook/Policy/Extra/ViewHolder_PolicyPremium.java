package com.itshiteshverma.bankblackbook.Policy.Extra;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itshiteshverma.bankblackbook.R;

import java.text.DecimalFormat;


/**
 * Created by Wilmar Africa Ltd on 25-07-17.
 */


public class ViewHolder_PolicyPremium extends RecyclerView.ViewHolder {

    View mView;
    TextView ReciptNo, PremiumDate, AmountPaid, Remarks;
    public ImageView Delete, PaymentMode;
//    LinearLayout mainLayout;

    public ViewHolder_PolicyPremium(View itemView) {
        super(itemView);
        mView = itemView;
        //  mainLayout = mView.findViewById(R.id.mainLayoutCard_premium);
        ReciptNo = mView.findViewById(R.id.tvReciptNo);
        PremiumDate = mView.findViewById(R.id.tvDate);
        AmountPaid = mView.findViewById(R.id.tvAmountPaid);
        PaymentMode = mView.findViewById(R.id.imageViewMode);
        Remarks = mView.findViewById(R.id.tvRemarks_premiumData);
        Delete = mView.findViewById(R.id.imageViewDelete);


    }

    public void setReciptNo(String reciptNo) {
        this.ReciptNo.setText(reciptNo);
    }

    public void setPremiumDate(String premiumDate) {
        this.PremiumDate.setText(premiumDate);
    }

    public void setAmountPaid(String amountPaid) {
        this.AmountPaid.setText(new DecimalFormat("##,##,##0").format(Integer.parseInt(amountPaid)));
    }

    public void setPaymentMode(String paymentMode) {
        switch (paymentMode) {
            case "Cash/Direct   ":
                this.PaymentMode.setImageResource(R.drawable.ic_icons8_initiate_money_transfer);
                break;
            case "Cheque/DD   ":
                this.PaymentMode.setImageResource(R.drawable.ic_icons8_paycheque);
                break;
            case "Net Banking / Card   ":
                this.PaymentMode.setImageResource(R.drawable.ic_icons8_bank_cards);
                break;

            default:
                this.PaymentMode.setImageResource(R.drawable.ic_icons8_repeat);
        }

    }

    public void setRemarks(String remarks) {
        this.Remarks.setText(remarks);
    }

}
