package ttit.com.shuvo.docdiary.appointment_admin.arraylists;

public class PaymentForAppList {
    private String prm_id;
    private String prm_code;
    private String prm_date;

    public PaymentForAppList(String prm_id, String prm_code, String prm_date) {
        this.prm_id = prm_id;
        this.prm_code = prm_code;
        this.prm_date = prm_date;
    }

    public String getPrm_id() {
        return prm_id;
    }

    public void setPrm_id(String prm_id) {
        this.prm_id = prm_id;
    }

    public String getPrm_code() {
        return prm_code;
    }

    public void setPrm_code(String prm_code) {
        this.prm_code = prm_code;
    }

    public String getPrm_date() {
        return prm_date;
    }

    public void setPrm_date(String prm_date) {
        this.prm_date = prm_date;
    }
}
