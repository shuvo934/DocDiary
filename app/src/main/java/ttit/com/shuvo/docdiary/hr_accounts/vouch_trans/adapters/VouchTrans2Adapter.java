package ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.pay_invoice.PaymentInvoice;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists.VoucherLists2;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists.VoucherLists3;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.dialogs.VoucherDialog;

public class VouchTrans2Adapter extends RecyclerView.Adapter<VouchTrans2Adapter.VT2Holder> {

    private final ArrayList<VoucherLists2> mCategoryItem;
    private final Context myContext;

    public VouchTrans2Adapter(ArrayList<VoucherLists2> mCategoryItem, Context myContext) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public VT2Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.v_trans_view_2, parent, false);
        return new VT2Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VT2Holder holder, int position) {
        VoucherLists2 voucherLists2 = mCategoryItem.get(position);

        holder.voucherNo.setText(voucherLists2.getvNo());
        holder.particulars.setText(voucherLists2.getParticulars());

        ArrayList<VoucherLists3> voucherLists3s = voucherLists2.getVoucherLists3s();

        holder.itemDetails.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(myContext);
        holder.itemDetails.setLayoutManager(layoutManager);
        VouchTrans3Adapter vouchTrans3Adapter = new VouchTrans3Adapter(voucherLists3s,myContext);
        holder.itemDetails.setAdapter(vouchTrans3Adapter);

        if (position == mCategoryItem.size()-1) {
            holder.bottomborder.setVisibility(View.GONE);
        } else {
            holder.bottomborder.setVisibility(View.VISIBLE);
        }

//        if (voucherLists2.getType().equals("PRM") || voucherLists2.getType().equals("AV") /*|| voucherLists2.getType().equals("DPRCV") || voucherLists2.getType().equals("SM")*/) {
//            holder.voucherNo.setTextColor(myContext.getColor(R.color.green_sea));
//        }  else {
//            holder.voucherNo.setTextColor(myContext.getColor(R.color.default_text_color));
//        }

        if (voucherLists2.getType().equals("PRM") /*|| voucherLists2.getType().equals("DPRCV") || voucherLists2.getType().equals("SM")*/) {
            if (!voucherLists2.getPay_type_flag().isEmpty() && voucherLists2.getPay_type_flag().equals("1")) {
                holder.voucherNo.setTextColor(myContext.getColor(R.color.green_sea));
            }
            else {
                holder.voucherNo.setTextColor(myContext.getColor(R.color.default_text_color));
            }
        }
        else if (voucherLists2.getType().equals("AV")) {
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

    public class VT2Holder extends RecyclerView.ViewHolder {
        public TextView voucherNo;
        public TextView particulars;

        public RecyclerView itemDetails;

        public LinearLayout bottomborder;


        public VT2Holder(@NonNull View itemView) {
            super(itemView);
            voucherNo = itemView.findViewById(R.id.voucher_no_v_trans);
            particulars = itemView.findViewById(R.id.particulars_v_trans);

            itemDetails = itemView.findViewById(R.id.voucher_trans_report_view3);
            bottomborder = itemView.findViewById(R.id.layout_bottom_border_v_trans);

            voucherNo.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                if (mCategoryItem.get(getAdapterPosition()).getType().equals("PRM")) {
                    if (mCategoryItem.get(getAdapterPosition()).getPay_type_flag().equals("1")) {
                        Intent intent = new Intent(myContext, PaymentInvoice.class);
                        intent.putExtra("PRM_CODE",mCategoryItem.get(getAdapterPosition()).getOrderNo());
                        activity.startActivity(intent);
                    }
                }
                else if (mCategoryItem.get(getAdapterPosition()).getType().equals("AV")) {
                    VoucherDialog debitCreditVoucher = new VoucherDialog(myContext, mCategoryItem.get(getAdapterPosition()).getOrderNo());
                    debitCreditVoucher.show(activity.getSupportFragmentManager(),"VTDV");

                }
//                else if (mCategoryItem.get(getAdapterPosition()).getType().equals("DPRCV")) {
//                    VM_NO = mCategoryItem.get(getAdapterPosition()).getOrderNo();
//
//                    DirectPurchase directPurchase = new DirectPurchase(myContext);
//                    directPurchase.show(activity.getSupportFragmentManager(),"DPR");
//                }
//                else if (mCategoryItem.get(getAdapterPosition()).getType().equals("SM")) {
//                    for (int i = 0; i < deliveryChallanListsVT.size(); i++) {
//                        if (mCategoryItem.get(getAdapterPosition()).getOrderNo().equals(deliveryChallanListsVT.get(i).getDelivery_no())) {
//
//                            INV_NO = deliveryChallanListsVT.get(i).getDelivery_no();
//                            INV_DATE = deliveryChallanListsVT.get(i).getDelivery_date();
//                            SO_NO = deliveryChallanListsVT.get(i).getOrder_no();
//                            SO_DATE = deliveryChallanListsVT.get(i).getOrder_date();
//                            C_NAME = deliveryChallanListsVT.get(i).getClient_name();
//                            C_CODE = deliveryChallanListsVT.get(i).getAd_code();
//                            ADDS = deliveryChallanListsVT.get(i).getTarget_address();
//                            EDD = deliveryChallanListsVT.get(i).getEdd();
//                            C_EMAIL = deliveryChallanListsVT.get(i).getContact_email();
//                            PERSON = deliveryChallanListsVT.get(i).getContact_person();
//                            CONTACT = deliveryChallanListsVT.get(i).getContact_number();
//                            SM_ID = deliveryChallanListsVT.get(i).getSm_id();
//                            VAT_AMNT_DC = deliveryChallanListsVT.get(i).getVat_amnt();
//                            fromVTSO = 1;
//
//                            DeliveryChallanDetails deliveryChallanDetails = new DeliveryChallanDetails(myContext);
//                            deliveryChallanDetails.show(activity.getSupportFragmentManager(),"DCDVT");
//                            break;
//                        }
//                    }
//                }
            });

        }
    }
}
