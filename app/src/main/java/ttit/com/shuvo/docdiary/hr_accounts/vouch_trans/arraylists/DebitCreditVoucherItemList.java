package ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists;

public class DebitCreditVoucherItemList {
    private final String vm_no;
    private final String vm_date;
    private final String vm_type;
    private final String vm_bill_ref_no;
    private final String vm_bill_ref_date;
    private final String vm_naration;
    private final String ad_code;
    private final String ad_name;
    private final String vd_dr_amt;
    private final String vd_cr_amt;

    public DebitCreditVoucherItemList(String vm_no, String vm_date, String vm_type, String vm_bill_ref_no, String vm_bill_ref_date, String vm_naration, String ad_code, String ad_name, String vd_dr_amt, String vd_cr_amt) {
        this.vm_no = vm_no;
        this.vm_date = vm_date;
        this.vm_type = vm_type;
        this.vm_bill_ref_no = vm_bill_ref_no;
        this.vm_bill_ref_date = vm_bill_ref_date;
        this.vm_naration = vm_naration;
        this.ad_code = ad_code;
        this.ad_name = ad_name;
        this.vd_dr_amt = vd_dr_amt;
        this.vd_cr_amt = vd_cr_amt;
    }

    public String getVm_no() {
        return vm_no;
    }

    public String getVm_date() {
        return vm_date;
    }

    public String getVm_type() {
        return vm_type;
    }

    public String getVm_bill_ref_no() {
        return vm_bill_ref_no;
    }

    public String getVm_bill_ref_date() {
        return vm_bill_ref_date;
    }

    public String getVm_naration() {
        return vm_naration;
    }

    public String getAd_code() {
        return ad_code;
    }

    public String getAd_name() {
        return ad_name;
    }

    public String getVd_dr_amt() {
        return vd_dr_amt;
    }

    public String getVd_cr_amt() {
        return vd_cr_amt;
    }
}
