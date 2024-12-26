package ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists;

public class VoucherLists3 {

    private String accountDetails;
    private String debit;
    private String credit;

    public VoucherLists3(String accountDetails, String debit, String credit) {
        this.accountDetails = accountDetails;
        this.debit = debit;
        this.credit = credit;
    }

    public String getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(String accountDetails) {
        this.accountDetails = accountDetails;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }
}
