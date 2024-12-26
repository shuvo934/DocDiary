package ttit.com.shuvo.docdiary.hr_accounts.income_expense.arraylists;

public class IncExpStatementList {
    private final String lvl1;
    private final String lvl2;
    private final String lvl3;
    private String bfdr;
    private String bfcr;
    private String curdr;
    private String curcr;
    private final String lg_ad_id;
    private final String ah_code;
    private final String al1_code;
    private final String al2_code;
    private final String ad_code;
    private String level_type;

    public IncExpStatementList(String lvl1, String lvl2, String lvl3, String bfdr, String bfcr,
                               String curdr, String curcr, String lg_ad_id, String ah_code,
                               String al1_code, String al2_code, String ad_code, String level_type) {
        this.lvl1 = lvl1;
        this.lvl2 = lvl2;
        this.lvl3 = lvl3;
        this.bfdr = bfdr;
        this.bfcr = bfcr;
        this.curdr = curdr;
        this.curcr = curcr;
        this.lg_ad_id = lg_ad_id;
        this.ah_code = ah_code;
        this.al1_code = al1_code;
        this.al2_code = al2_code;
        this.ad_code = ad_code;
        this.level_type = level_type;
    }

    public void setBfdr(String bfdr) {
        this.bfdr = bfdr;
    }

    public void setBfcr(String bfcr) {
        this.bfcr = bfcr;
    }

    public void setCurdr(String curdr) {
        this.curdr = curdr;
    }

    public void setCurcr(String curcr) {
        this.curcr = curcr;
    }

    public String getLevel_type() {
        return level_type;
    }

    public void setLevel_type(String level_type) {
        this.level_type = level_type;
    }

    public String getLvl1() {
        return lvl1;
    }

    public String getLvl2() {
        return lvl2;
    }

    public String getLvl3() {
        return lvl3;
    }

    public String getBfdr() {
        return bfdr;
    }

    public String getBfcr() {
        return bfcr;
    }

    public String getCurdr() {
        return curdr;
    }

    public String getCurcr() {
        return curcr;
    }

    public String getLg_ad_id() {
        return lg_ad_id;
    }

    public String getAh_code() {
        return ah_code;
    }

    public String getAl1_code() {
        return al1_code;
    }

    public String getAl2_code() {
        return al2_code;
    }

    public String getAd_code() {
        return ad_code;
    }
}
