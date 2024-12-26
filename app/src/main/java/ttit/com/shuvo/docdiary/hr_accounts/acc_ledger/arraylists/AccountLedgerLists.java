package ttit.com.shuvo.docdiary.hr_accounts.acc_ledger.arraylists;

public class AccountLedgerLists {
    private String lgId;
    private String date;
    private String voucherNo;
    private String particulars;
    private String lgDebit;
    private String debit;
    private String lgCredit;
    private String credit;
    private String openingDebit;
    private String openingCredit;
    private String ahCode;
    private String balance;
    private String lg_trans_type;
    private String lg_inv_pur_no;
    private String prm_pay_type_flag;
    private boolean updated;

    public AccountLedgerLists(String lgId, String date, String voucherNo, String particulars, String lgDebit,
                              String debit, String lgCredit, String credit, String openingDebit, String openingCredit,
                              String ahCode, String balance, String lg_trans_type, String lg_inv_pur_no, String prm_pay_type_flag, boolean updated) {
        this.lgId = lgId;
        this.date = date;
        this.voucherNo = voucherNo;
        this.particulars = particulars;
        this.lgDebit = lgDebit;
        this.debit = debit;
        this.lgCredit = lgCredit;
        this.credit = credit;
        this.openingDebit = openingDebit;
        this.openingCredit = openingCredit;
        this.ahCode = ahCode;
        this.balance = balance;
        this.lg_trans_type = lg_trans_type;
        this.lg_inv_pur_no = lg_inv_pur_no;
        this.prm_pay_type_flag = prm_pay_type_flag;
        this.updated = updated;
    }

    public String getLgId() {
        return lgId;
    }

    public void setLgId(String lgId) {
        this.lgId = lgId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public String getLgDebit() {
        return lgDebit;
    }

    public void setLgDebit(String lgDebit) {
        this.lgDebit = lgDebit;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getLgCredit() {
        return lgCredit;
    }

    public void setLgCredit(String lgCredit) {
        this.lgCredit = lgCredit;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getOpeningDebit() {
        return openingDebit;
    }

    public void setOpeningDebit(String openingDebit) {
        this.openingDebit = openingDebit;
    }

    public String getOpeningCredit() {
        return openingCredit;
    }

    public void setOpeningCredit(String openingCredit) {
        this.openingCredit = openingCredit;
    }

    public String getAhCode() {
        return ahCode;
    }

    public void setAhCode(String ahCode) {
        this.ahCode = ahCode;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getLg_trans_type() {
        return lg_trans_type;
    }

    public void setLg_trans_type(String lg_trans_type) {
        this.lg_trans_type = lg_trans_type;
    }

    public String getLg_inv_pur_no() {
        return lg_inv_pur_no;
    }

    public void setLg_inv_pur_no(String lg_inv_pur_no) {
        this.lg_inv_pur_no = lg_inv_pur_no;
    }

    public String getPrm_pay_type_flag() {
        return prm_pay_type_flag;
    }

    public void setPrm_pay_type_flag(String prm_pay_type_flag) {
        this.prm_pay_type_flag = prm_pay_type_flag;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
