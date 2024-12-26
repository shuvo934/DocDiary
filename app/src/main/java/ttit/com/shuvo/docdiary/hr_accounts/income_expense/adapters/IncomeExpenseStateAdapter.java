package ttit.com.shuvo.docdiary.hr_accounts.income_expense.adapters;

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
import ttit.com.shuvo.docdiary.hr_accounts.acc_ledger.AccountLedger;
import ttit.com.shuvo.docdiary.hr_accounts.income_expense.arraylists.IncExpStatementList;

public class IncomeExpenseStateAdapter extends RecyclerView.Adapter<IncomeExpenseStateAdapter.ISAHolder> {
    private final ArrayList<IncExpStatementList> mCategoryItem;
    private final Context myContext;
    private final String firstDate;
    private final String lastDate;

    public IncomeExpenseStateAdapter(ArrayList<IncExpStatementList> mCategoryItem, Context myContext, String firstDate, String lastDate) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
    }

    @NonNull
    @Override
    public ISAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.income_statement_details_layout, parent, false);
        return new ISAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ISAHolder holder, int position) {
        IncExpStatementList statementList = mCategoryItem.get(position);
        String level = statementList.getLevel_type();
        if (level.equals("3")) {
            if (position == 0) {
                String head_account = statementList.getLvl1() + "\n      " + statementList.getLvl2();
                holder.headAccount.setText(head_account);
                holder.headAccount.setVisibility(View.VISIBLE);

                String head_acc_3 = "            "+statementList.getLvl3() + " >>";
                holder.headAccount3.setText(head_acc_3);
                holder.headAccount3.setVisibility(View.VISIBLE);

                DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

                double b_debit = Double.parseDouble(statementList.getBfdr());
                double b_credit = Double.parseDouble(statementList.getBfcr());
                double c_debit = Double.parseDouble(statementList.getCurdr());
                double c_credit = Double.parseDouble(statementList.getCurcr());

                String bfr_debit = formatter.format(b_debit);
                String bfr_credit = formatter.format(b_credit);
                String dur_debit = formatter.format(c_debit);
                String dur_credit = formatter.format(c_credit);

                if (bfr_debit.equals("0")) {
                    holder.bfrDebit.setText("");
                }
                else {
                    holder.bfrDebit.setText(bfr_debit);
                }

                if (bfr_credit.equals("0")) {
                    holder.bfrCredit.setText("");
                }
                else {
                    holder.bfrCredit.setText(bfr_credit);
                }

                if (dur_debit.equals("0")) {
                    holder.durDebit.setText("");
                }
                else {
                    holder.durDebit.setText(dur_debit);
                }

                if (dur_credit.equals("0")) {
                    holder.durCredit.setText("");
                }
                else {
                    holder.durCredit.setText(dur_credit);
                }

                double balance = (b_credit + c_credit) - (b_debit + c_debit);
                String bal = formatter.format(balance);
                bal = bal.replace("-","");

                if (bal.equals("0")) {
                    holder.balance.setText("");
                }
                else {
                    holder.balance.setText(bal);
                }

                if (position == mCategoryItem.size() - 1) {
                    holder.itemDivider.setVisibility(View.VISIBLE);
                }
                else if (position < mCategoryItem.size() - 1) {

                    int index = position + 1;

                    String next_al1_code = mCategoryItem.get(index).getAl1_code();

                    String al1_code = statementList.getAl1_code();

                    if (al1_code.equals(next_al1_code)) {
                        holder.itemDivider.setVisibility(View.GONE);
                    }
                    else {
                        holder.itemDivider.setVisibility(View.VISIBLE);
                    }
                }
            }
            else {
                int index = position - 1;

                String pre_al1_code = mCategoryItem.get(index).getAl1_code();
                String pre_al2_code = mCategoryItem.get(index).getAl2_code();

                String al1_code = statementList.getAl1_code();
                String al2_code = statementList.getAl2_code();

                if (pre_al1_code.equals(al1_code)) {
                    if (pre_al2_code.equals(al2_code)) {
                        holder.headAccount.setVisibility(View.GONE);

                        String head_acc_3 = "            "+statementList.getLvl3() + " >>";
                        holder.headAccount3.setText(head_acc_3);
                        holder.headAccount3.setVisibility(View.VISIBLE);
                    }
                    else {
                        String head_account = "      " + statementList.getLvl2();
                        holder.headAccount.setText(head_account);
                        holder.headAccount.setVisibility(View.VISIBLE);

                        String head_acc_3 = "            "+statementList.getLvl3() + " >>";
                        holder.headAccount3.setText(head_acc_3);
                        holder.headAccount3.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    String head_account = statementList.getLvl1() + "\n      " + statementList.getLvl2();
                    holder.headAccount.setText(head_account);
                    holder.headAccount.setVisibility(View.VISIBLE);

                    String head_acc_3 = "            "+statementList.getLvl3() + " >>";
                    holder.headAccount3.setText(head_acc_3);
                    holder.headAccount3.setVisibility(View.VISIBLE);
                }

                DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

                double b_debit = Double.parseDouble(statementList.getBfdr());
                double b_credit = Double.parseDouble(statementList.getBfcr());
                double c_debit = Double.parseDouble(statementList.getCurdr());
                double c_credit = Double.parseDouble(statementList.getCurcr());

                String bfr_debit = formatter.format(b_debit);
                String bfr_credit = formatter.format(b_credit);
                String dur_debit = formatter.format(c_debit);
                String dur_credit = formatter.format(c_credit);

                if (bfr_debit.equals("0")) {
                    holder.bfrDebit.setText("");
                }
                else {
                    holder.bfrDebit.setText(bfr_debit);
                }

                if (bfr_credit.equals("0")) {
                    holder.bfrCredit.setText("");
                }
                else {
                    holder.bfrCredit.setText(bfr_credit);
                }

                if (dur_debit.equals("0")) {
                    holder.durDebit.setText("");
                }
                else {
                    holder.durDebit.setText(dur_debit);
                }

                if (dur_credit.equals("0")) {
                    holder.durCredit.setText("");
                }
                else {
                    holder.durCredit.setText(dur_credit);
                }

                double balance = (b_credit + c_credit) - (b_debit + c_debit);
                String bal = formatter.format(balance);
                bal = bal.replace("-","");

                if (bal.equals("0")) {
                    holder.balance.setText("");
                }
                else {
                    holder.balance.setText(bal);
                }

                if (position == mCategoryItem.size() - 1) {
                    holder.itemDivider.setVisibility(View.VISIBLE);
                }
                else if (position != mCategoryItem.size() - 1 && position < mCategoryItem.size() - 1 ) {
                    int dex = position + 1;
                    String next_al1_code = mCategoryItem.get(dex).getAl1_code();

                    if (al1_code.equals(next_al1_code)) {
                        holder.itemDivider.setVisibility(View.GONE);
                    }
                    else {
                        holder.itemDivider.setVisibility(View.VISIBLE);
                    }
                }

            }
        }
        else if (level.equals("2")) {
            if (position == 0) {
                String head_account = statementList.getLvl1() + "\n      " + statementList.getLvl2();
                holder.headAccount.setText(head_account);
                holder.headAccount.setVisibility(View.VISIBLE);

                holder.headAccount3.setVisibility(View.GONE);

                DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

                double b_debit = Double.parseDouble(statementList.getBfdr());
                double b_credit = Double.parseDouble(statementList.getBfcr());
                double c_debit = Double.parseDouble(statementList.getCurdr());
                double c_credit = Double.parseDouble(statementList.getCurcr());

                String bfr_debit = formatter.format(b_debit);
                String bfr_credit = formatter.format(b_credit);
                String dur_debit = formatter.format(c_debit);
                String dur_credit = formatter.format(c_credit);

                if (bfr_debit.equals("0")) {
                    holder.bfrDebit.setText("");
                }
                else {
                    holder.bfrDebit.setText(bfr_debit);
                }

                if (bfr_credit.equals("0")) {
                    holder.bfrCredit.setText("");
                }
                else {
                    holder.bfrCredit.setText(bfr_credit);
                }

                if (dur_debit.equals("0")) {
                    holder.durDebit.setText("");
                }
                else {
                    holder.durDebit.setText(dur_debit);
                }

                if (dur_credit.equals("0")) {
                    holder.durCredit.setText("");
                }
                else {
                    holder.durCredit.setText(dur_credit);
                }

                double balance = (b_credit + c_credit) - (b_debit + c_debit);
                String bal = formatter.format(balance);
                bal = bal.replace("-","");

                if (bal.equals("0")) {
                    holder.balance.setText("");
                }
                else {
                    holder.balance.setText(bal);
                }

                if (position == mCategoryItem.size() - 1) {
                    holder.itemDivider.setVisibility(View.VISIBLE);
                }
                else if (position < mCategoryItem.size() - 1) {

                    int index = position + 1;

                    String next_al1_code = mCategoryItem.get(index).getAl1_code();

                    String al1_code = statementList.getAl1_code();

                    if (al1_code.equals(next_al1_code)) {
                        holder.itemDivider.setVisibility(View.GONE);
                    }
                    else {
                        holder.itemDivider.setVisibility(View.VISIBLE);
                    }
                }
            }
            else {
                int index = position - 1;

                String pre_al1_code = mCategoryItem.get(index).getAl1_code();

                String al1_code = statementList.getAl1_code();

                String head_account;
                if (pre_al1_code.equals(al1_code)) {
                    head_account = "      " + statementList.getLvl2();
                }
                else {
                    head_account = statementList.getLvl1() + "\n      " + statementList.getLvl2();
                }
                holder.headAccount.setText(head_account);
                holder.headAccount.setVisibility(View.VISIBLE);
                holder.headAccount3.setVisibility(View.GONE);

                DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

                double b_debit = Double.parseDouble(statementList.getBfdr());
                double b_credit = Double.parseDouble(statementList.getBfcr());
                double c_debit = Double.parseDouble(statementList.getCurdr());
                double c_credit = Double.parseDouble(statementList.getCurcr());

                String bfr_debit = formatter.format(b_debit);
                String bfr_credit = formatter.format(b_credit);
                String dur_debit = formatter.format(c_debit);
                String dur_credit = formatter.format(c_credit);

                if (bfr_debit.equals("0")) {
                    holder.bfrDebit.setText("");
                }
                else {
                    holder.bfrDebit.setText(bfr_debit);
                }

                if (bfr_credit.equals("0")) {
                    holder.bfrCredit.setText("");
                }
                else {
                    holder.bfrCredit.setText(bfr_credit);
                }

                if (dur_debit.equals("0")) {
                    holder.durDebit.setText("");
                }
                else {
                    holder.durDebit.setText(dur_debit);
                }

                if (dur_credit.equals("0")) {
                    holder.durCredit.setText("");
                }
                else {
                    holder.durCredit.setText(dur_credit);
                }

                double balance = (b_credit + c_credit) - (b_debit + c_debit);
                String bal = formatter.format(balance);
                bal = bal.replace("-","");

                if (bal.equals("0")) {
                    holder.balance.setText("");
                }
                else {
                    holder.balance.setText(bal);
                }

                if (position == mCategoryItem.size() - 1) {
                    holder.itemDivider.setVisibility(View.VISIBLE);
                }
                else if (position != mCategoryItem.size() - 1 && position < mCategoryItem.size() - 1 ) {
                    int dex = position + 1;
                    String next_al1_code = mCategoryItem.get(dex).getAl1_code();

                    if (al1_code.equals(next_al1_code)) {
                        holder.itemDivider.setVisibility(View.GONE);
                    }
                    else {
                        holder.itemDivider.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        else {
            if (position == 0) {
                String head_account = statementList.getLvl1();
                holder.headAccount.setText(head_account);
                holder.headAccount.setVisibility(View.VISIBLE);

                holder.headAccount3.setVisibility(View.GONE);

                DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

                double b_debit = Double.parseDouble(statementList.getBfdr());
                double b_credit = Double.parseDouble(statementList.getBfcr());
                double c_debit = Double.parseDouble(statementList.getCurdr());
                double c_credit = Double.parseDouble(statementList.getCurcr());

                String bfr_debit = formatter.format(b_debit);
                String bfr_credit = formatter.format(b_credit);
                String dur_debit = formatter.format(c_debit);
                String dur_credit = formatter.format(c_credit);

                if (bfr_debit.equals("0")) {
                    holder.bfrDebit.setText("");
                }
                else {
                    holder.bfrDebit.setText(bfr_debit);
                }

                if (bfr_credit.equals("0")) {
                    holder.bfrCredit.setText("");
                }
                else {
                    holder.bfrCredit.setText(bfr_credit);
                }

                if (dur_debit.equals("0")) {
                    holder.durDebit.setText("");
                }
                else {
                    holder.durDebit.setText(dur_debit);
                }

                if (dur_credit.equals("0")) {
                    holder.durCredit.setText("");
                }
                else {
                    holder.durCredit.setText(dur_credit);
                }

                double balance = (b_credit + c_credit) - (b_debit + c_debit);
                String bal = formatter.format(balance);
                bal = bal.replace("-","");

                if (bal.equals("0")) {
                    holder.balance.setText("");
                }
                else {
                    holder.balance.setText(bal);
                }

                if (position == mCategoryItem.size() - 1) {
                    holder.itemDivider.setVisibility(View.VISIBLE);
                }
                else if (position < mCategoryItem.size() - 1) {

                    int index = position + 1;

                    String next_al1_code = mCategoryItem.get(index).getAl1_code();

                    String al1_code = statementList.getAl1_code();

                    if (al1_code.equals(next_al1_code)) {
                        holder.itemDivider.setVisibility(View.GONE);
                    }
                    else {
                        holder.itemDivider.setVisibility(View.VISIBLE);
                    }
                }
            }
            else {
                String al1_code = statementList.getAl1_code();

                String head_account = statementList.getLvl1();
                holder.headAccount.setText(head_account);
                holder.headAccount.setVisibility(View.VISIBLE);

                holder.headAccount3.setVisibility(View.GONE);

                DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

                double b_debit = Double.parseDouble(statementList.getBfdr());
                double b_credit = Double.parseDouble(statementList.getBfcr());
                double c_debit = Double.parseDouble(statementList.getCurdr());
                double c_credit = Double.parseDouble(statementList.getCurcr());

                String bfr_debit = formatter.format(b_debit);
                String bfr_credit = formatter.format(b_credit);
                String dur_debit = formatter.format(c_debit);
                String dur_credit = formatter.format(c_credit);

                if (bfr_debit.equals("0")) {
                    holder.bfrDebit.setText("");
                }
                else {
                    holder.bfrDebit.setText(bfr_debit);
                }

                if (bfr_credit.equals("0")) {
                    holder.bfrCredit.setText("");
                }
                else {
                    holder.bfrCredit.setText(bfr_credit);
                }

                if (dur_debit.equals("0")) {
                    holder.durDebit.setText("");
                }
                else {
                    holder.durDebit.setText(dur_debit);
                }

                if (dur_credit.equals("0")) {
                    holder.durCredit.setText("");
                }
                else {
                    holder.durCredit.setText(dur_credit);
                }

                double balance = (b_credit + c_credit) - (b_debit + c_debit);
                String bal = formatter.format(balance);
                bal = bal.replace("-","");

                if (bal.equals("0")) {
                    holder.balance.setText("");
                }
                else {
                    holder.balance.setText(bal);
                }

                if (position == mCategoryItem.size() - 1) {
                    holder.itemDivider.setVisibility(View.VISIBLE);
                }
                else if (position != mCategoryItem.size() - 1 && position < mCategoryItem.size() - 1 ) {
                    int dex = position + 1;
                    String next_al1_code = mCategoryItem.get(dex).getAl1_code();

                    if (al1_code.equals(next_al1_code)) {
                        holder.itemDivider.setVisibility(View.GONE);
                    }
                    else {
                        holder.itemDivider.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public class ISAHolder extends RecyclerView.ViewHolder {

        public TextView headAccount;
        public TextView headAccount3;
        public TextView bfrDebit;
        public TextView bfrCredit;
        public TextView durDebit;
        public TextView durCredit;
        public TextView balance;
        public LinearLayout itemDivider;

        public ISAHolder(@NonNull View itemView) {
            super(itemView);
            headAccount = itemView.findViewById(R.id.head_of_account_name_income_state);
            headAccount3 = itemView.findViewById(R.id.head_of_level_3_account_name_income_state);
            bfrDebit = itemView.findViewById(R.id.before_debit_value_income_state);
            bfrCredit = itemView.findViewById(R.id.before_credit_value_income_state);
            durDebit = itemView.findViewById(R.id.during_debit_value_income_state);
            durCredit = itemView.findViewById(R.id.during_credit_value_income_state);
            balance = itemView.findViewById(R.id.balance_of_debit_credit_income_state);
            itemDivider = itemView.findViewById(R.id.layout_bottom_border_income_state_first);

            headAccount3.setOnClickListener(view -> {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();

                String ad_id = mCategoryItem.get(getAdapterPosition()).getLg_ad_id();

                Intent intent = new Intent(myContext, AccountLedger.class);
                intent.putExtra("FIRST_DATE",firstDate);
                intent.putExtra("LAST_DATE",lastDate);
                intent.putExtra("AD_ID",ad_id);
                activity.startActivity(intent);
            });

        }
    }
}
