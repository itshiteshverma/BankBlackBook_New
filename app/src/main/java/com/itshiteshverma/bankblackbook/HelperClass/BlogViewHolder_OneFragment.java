package com.itshiteshverma.bankblackbook.HelperClass;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itshiteshverma.bankblackbook.R;

/**
 * Created by Wilmar Africa Ltd on 25-07-17.
 */


public class BlogViewHolder_OneFragment extends RecyclerView.ViewHolder {

    public View mView;
    TextView Policy_Name, DueDate, BankName, typeofDate;
    LinearLayout mainLayout;

    public BlogViewHolder_OneFragment(View itemView) {
        super(itemView);
        mView = itemView;
        Policy_Name = mView.findViewById(R.id.tvPolicyName);
        typeofDate = mView.findViewById(R.id.tv_typeofDate);
        DueDate = mView.findViewById(R.id.tvDueDate);
        mainLayout = mView.findViewById(R.id.mainLayoutCard);
        BankName = mView.findViewById(R.id.tvBankName);
    }

    public void setPolicy_Name(String Policy_Name) {

        this.Policy_Name.setText(Policy_Name);
    }

    public void setDue_date(String due_date) {

        this.DueDate.setText(due_date);
    }

    public void setBank_Name(String bank_name) {

        this.BankName.setText(bank_name);
    }

    public void setBackgroundColor(String type) {
        switch (type) {
            case "POLICY":
                // this.mainLayout.setBackgroundColor(Color.parseColor("#a91aa6f1"));
                this.mainLayout.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.border_round_green));
                break;

            case "FD":
                this.mainLayout.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.border_round_red));
                this.typeofDate.setText("Maturity Date");
                break;

        }

    }


}
