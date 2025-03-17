package ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists;

public class VoucherAllData {
    private String vdate;
    private String lg_voucher_no;
    private String lg_particulars;
    private String lg_inv_pur_no;
    private String lg_trans_type;
    private String prm_pay_type_flag;
    private String ad_dtl;
    private String lg_dr_amt;
    private String lg_cr_amt;
    private String lg_voucher_type;

    public VoucherAllData(String vdate, String lg_voucher_no, String lg_particulars, String lg_inv_pur_no, String lg_trans_type,
                          String prm_pay_type_flag, String ad_dtl, String lg_dr_amt, String lg_cr_amt, String lg_voucher_type) {
        this.vdate = vdate;
        this.lg_voucher_no = lg_voucher_no;
        this.lg_particulars = lg_particulars;
        this.lg_inv_pur_no = lg_inv_pur_no;
        this.lg_trans_type = lg_trans_type;
        this.prm_pay_type_flag = prm_pay_type_flag;
        this.ad_dtl = ad_dtl;
        this.lg_dr_amt = lg_dr_amt;
        this.lg_cr_amt = lg_cr_amt;
        this.lg_voucher_type = lg_voucher_type;
    }

    public String getVdate() {
        return vdate;
    }

    public void setVdate(String vdate) {
        this.vdate = vdate;
    }

    public String getLg_voucher_no() {
        return lg_voucher_no;
    }

    public void setLg_voucher_no(String lg_voucher_no) {
        this.lg_voucher_no = lg_voucher_no;
    }

    public String getLg_particulars() {
        return lg_particulars;
    }

    public void setLg_particulars(String lg_particulars) {
        this.lg_particulars = lg_particulars;
    }

    public String getLg_inv_pur_no() {
        return lg_inv_pur_no;
    }

    public void setLg_inv_pur_no(String lg_inv_pur_no) {
        this.lg_inv_pur_no = lg_inv_pur_no;
    }

    public String getLg_trans_type() {
        return lg_trans_type;
    }

    public void setLg_trans_type(String lg_trans_type) {
        this.lg_trans_type = lg_trans_type;
    }

    public String getPrm_pay_type_flag() {
        return prm_pay_type_flag;
    }

    public void setPrm_pay_type_flag(String prm_pay_type_flag) {
        this.prm_pay_type_flag = prm_pay_type_flag;
    }

    public String getAd_dtl() {
        return ad_dtl;
    }

    public void setAd_dtl(String ad_dtl) {
        this.ad_dtl = ad_dtl;
    }

    public String getLg_dr_amt() {
        return lg_dr_amt;
    }

    public void setLg_dr_amt(String lg_dr_amt) {
        this.lg_dr_amt = lg_dr_amt;
    }

    public String getLg_cr_amt() {
        return lg_cr_amt;
    }

    public void setLg_cr_amt(String lg_cr_amt) {
        this.lg_cr_amt = lg_cr_amt;
    }

    public String getLg_voucher_type() {
        return lg_voucher_type;
    }

    public void setLg_voucher_type(String lg_voucher_type) {
        this.lg_voucher_type = lg_voucher_type;
    }
}
