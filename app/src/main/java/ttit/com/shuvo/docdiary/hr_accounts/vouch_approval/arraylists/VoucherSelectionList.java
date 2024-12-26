package ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.arraylists;

public class VoucherSelectionList {
    private String vm_id;
    private String vm_no;
    private String vm_date;
    private String vm_type;
    private String vm_bill_ref_no;
    private String vm_naration;
    private String bill_date;
    private String vm_voucher_approved_flag;
    private String amount;
    private String status;
    private String approvedUser;
    private String approved_date;

    public VoucherSelectionList(String vm_id, String vm_no, String vm_date, String vm_type,
                                String vm_bill_ref_no, String vm_naration, String bill_date,
                                String vm_voucher_approved_flag, String amount, String status,
                                String approvedUser, String approved_date) {
        this.vm_id = vm_id;
        this.vm_no = vm_no;
        this.vm_date = vm_date;
        this.vm_type = vm_type;
        this.vm_bill_ref_no = vm_bill_ref_no;
        this.vm_naration = vm_naration;
        this.bill_date = bill_date;
        this.vm_voucher_approved_flag = vm_voucher_approved_flag;
        this.amount = amount;
        this.status = status;
        this.approvedUser = approvedUser;
        this.approved_date = approved_date;
    }

    public String getVm_id() {
        return vm_id;
    }

    public void setVm_id(String vm_id) {
        this.vm_id = vm_id;
    }

    public String getVm_no() {
        return vm_no;
    }

    public void setVm_no(String vm_no) {
        this.vm_no = vm_no;
    }

    public String getVm_date() {
        return vm_date;
    }

    public void setVm_date(String vm_date) {
        this.vm_date = vm_date;
    }

    public String getVm_type() {
        return vm_type;
    }

    public void setVm_type(String vm_type) {
        this.vm_type = vm_type;
    }

    public String getVm_bill_ref_no() {
        return vm_bill_ref_no;
    }

    public void setVm_bill_ref_no(String vm_bill_ref_no) {
        this.vm_bill_ref_no = vm_bill_ref_no;
    }

    public String getVm_naration() {
        return vm_naration;
    }

    public void setVm_naration(String vm_naration) {
        this.vm_naration = vm_naration;
    }

    public String getBill_date() {
        return bill_date;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }

    public String getVm_voucher_approved_flag() {
        return vm_voucher_approved_flag;
    }

    public void setVm_voucher_approved_flag(String vm_voucher_approved_flag) {
        this.vm_voucher_approved_flag = vm_voucher_approved_flag;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovedUser() {
        return approvedUser;
    }

    public void setApprovedUser(String approvedUser) {
        this.approvedUser = approvedUser;
    }

    public String getApproved_date() {
        return approved_date;
    }

    public void setApproved_date(String approved_date) {
        this.approved_date = approved_date;
    }
}
