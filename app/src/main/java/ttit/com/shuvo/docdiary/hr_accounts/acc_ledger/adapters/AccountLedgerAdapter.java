package ttit.com.shuvo.docdiary.hr_accounts.acc_ledger.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.acc_ledger.arraylists.AccountLedgerLists;
import ttit.com.shuvo.docdiary.hr_accounts.pay_invoice.PaymentInvoice;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.dialogs.VoucherDialog;

public class AccountLedgerAdapter extends RecyclerView.Adapter<AccountLedgerAdapter.ALAHolder> {

    private final ArrayList<AccountLedgerLists> mCategoryItem;
    private final Context myContext;

    public AccountLedgerAdapter(Context myContext, ArrayList<AccountLedgerLists> mCategoryItem) {
        this.myContext = myContext;
        this.mCategoryItem = mCategoryItem;
    }

    @NonNull
    @Override
    public ALAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.acc_ledger_item_view, parent, false);
        return new ALAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ALAHolder holder, int position) {
        AccountLedgerLists categoryItem = mCategoryItem.get(position);

        DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

        holder.vDate.setText(categoryItem.getDate());
        holder.voucherNo.setText(categoryItem.getVoucherNo());
        holder.particulars.setText(categoryItem.getParticulars());
        double debit = categoryItem.getDebit() != null ? Double.parseDouble(categoryItem.getDebit()) : 0.0;
        holder.alDebit.setText(formatter.format(debit));
        double credit = categoryItem.getCredit() != null ? Double.parseDouble(categoryItem.getCredit()) : 0.0;
        holder.alCredit.setText(formatter.format(credit));
        double bbl = categoryItem.getBalance() != null ? Double.parseDouble(categoryItem.getBalance()) : 0.0;
        String bal = formatter.format(bbl);

        if (bal.contains("-")) {
            bal = bal.replace("-","");
            bal = "(-)  "+ bal;
        }
        holder.alBalance.setText(bal);

        if (categoryItem.getLg_trans_type().equals("PRM") /*|| voucherLists2.getType().equals("DPRCV") || voucherLists2.getType().equals("SM")*/) {
            if (!categoryItem.getPrm_pay_type_flag().isEmpty() && categoryItem.getPrm_pay_type_flag().equals("1")) {
                holder.voucherNo.setTextColor(myContext.getColor(R.color.green_sea));
            }
            else {
                holder.voucherNo.setTextColor(myContext.getColor(R.color.default_text_color));
            }
        }
        else if (categoryItem.getLg_trans_type().equals("AV")) {
            holder.voucherNo.setTextColor(myContext.getColor(R.color.green_sea));
        }
        else {
            holder.voucherNo.setTextColor(myContext.getColor(R.color.default_text_color));
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public class ALAHolder extends RecyclerView.ViewHolder {
        public TextView vDate;
        public TextView voucherNo;
        public TextView particulars;
        public TextView alDebit;
        public TextView alCredit;
        public TextView alBalance;
        LinearLayout itemDivider;

        public ALAHolder(@NonNull View itemView) {
            super(itemView);
            vDate = itemView.findViewById(R.id.acc_led_date);
            voucherNo = itemView.findViewById(R.id.voucher_no_acc_led);
            particulars = itemView.findViewById(R.id.particulars_acc_led);
            alDebit = itemView.findViewById(R.id.debit_acc_led);
            alCredit = itemView.findViewById(R.id.credit_acc_led);
            alBalance = itemView.findViewById(R.id.balance_acc_led);
            itemDivider = itemView.findViewById(R.id.layout_bottom_border_acc_ledger);

            voucherNo.setOnClickListener(view -> {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                if (mCategoryItem.get(getAdapterPosition()).getLg_trans_type().equals("PRM")) {
                    if (mCategoryItem.get(getAdapterPosition()).getPrm_pay_type_flag().equals("1")) {
                        Intent intent = new Intent(myContext, PaymentInvoice.class);
                        intent.putExtra("PRM_CODE",mCategoryItem.get(getAdapterPosition()).getLg_inv_pur_no());
                        activity.startActivity(intent);
                    }
                }
                else if (mCategoryItem.get(getAdapterPosition()).getLg_trans_type().equals("AV")) {
                    VoucherDialog debitCreditVoucher = new VoucherDialog(myContext, mCategoryItem.get(getAdapterPosition()).getLg_inv_pur_no());
                    debitCreditVoucher.show(activity.getSupportFragmentManager(),"VTDV");
                }
            });

        }
    }
}
