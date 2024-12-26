package ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists;

import java.util.ArrayList;

public class VoucherLists1 {
    private String vDate;
    private ArrayList<VoucherLists2> voucherLists2s;
    private boolean updated;

    public VoucherLists1(String vDate, ArrayList<VoucherLists2> voucherLists2s, boolean updated) {
        this.vDate = vDate;
        this.voucherLists2s = voucherLists2s;
        this.updated = updated;
    }

    public String getvDate() {
        return vDate;
    }

    public void setvDate(String vDate) {
        this.vDate = vDate;
    }

    public ArrayList<VoucherLists2> getVoucherLists2s() {
        return voucherLists2s;
    }

    public void setVoucherLists2s(ArrayList<VoucherLists2> voucherLists2s) {
        this.voucherLists2s = voucherLists2s;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
