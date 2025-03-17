package ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists;

import java.util.ArrayList;

public class VoucherLists2 {

    private String vNo;
    private String particulars;
    private ArrayList<VoucherLists3> voucherLists3s;
    private String type;
    private String orderNo;
    private String pay_type_flag;
    private String lg_voucher_type;
    private boolean updated;

    public VoucherLists2(String vNo, String particulars, ArrayList<VoucherLists3> voucherLists3s, String type, String orderNo, String pay_type_flag, String lg_voucher_type, boolean updated) {
        this.vNo = vNo;
        this.particulars = particulars;
        this.voucherLists3s = voucherLists3s;
        this.type = type;
        this.orderNo = orderNo;
        this.pay_type_flag = pay_type_flag;
        this.lg_voucher_type = lg_voucher_type;
        this.updated = updated;
    }

    public String getvNo() {
        return vNo;
    }

    public void setvNo(String vNo) {
        this.vNo = vNo;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public ArrayList<VoucherLists3> getVoucherLists3s() {
        return voucherLists3s;
    }

    public void setVoucherLists3s(ArrayList<VoucherLists3> voucherLists3s) {
        this.voucherLists3s = voucherLists3s;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPay_type_flag() {
        return pay_type_flag;
    }

    public void setPay_type_flag(String pay_type_flag) {
        this.pay_type_flag = pay_type_flag;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getLg_voucher_type() {
        return lg_voucher_type;
    }

    public void setLg_voucher_type(String lg_voucher_type) {
        this.lg_voucher_type = lg_voucher_type;
    }
}
