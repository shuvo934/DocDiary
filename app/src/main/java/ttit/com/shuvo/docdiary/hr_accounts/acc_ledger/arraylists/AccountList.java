package ttit.com.shuvo.docdiary.hr_accounts.acc_ledger.arraylists;

public class AccountList {
    private final String ad_name;
    private final String ad_code;
    private final String al2_name;
    private final String al1_name;
    private final String ah_name;
    private final String lg_ad_id;
    private final String ah_code;

    public AccountList(String ad_name, String ad_code, String al2_name, String al1_name, String ah_name, String lg_ad_id, String ah_code) {
        this.ad_name = ad_name;
        this.ad_code = ad_code;
        this.al2_name = al2_name;
        this.al1_name = al1_name;
        this.ah_name = ah_name;
        this.lg_ad_id = lg_ad_id;
        this.ah_code = ah_code;
    }

    public String getAd_name() {
        return ad_name;
    }

    public String getAd_code() {
        return ad_code;
    }

    public String getAl2_name() {
        return al2_name;
    }

    public String getAl1_name() {
        return al1_name;
    }

    public String getAh_name() {
        return ah_name;
    }

    public String getLg_ad_id() {
        return lg_ad_id;
    }

    public String getAh_code() {
        return ah_code;
    }
}
