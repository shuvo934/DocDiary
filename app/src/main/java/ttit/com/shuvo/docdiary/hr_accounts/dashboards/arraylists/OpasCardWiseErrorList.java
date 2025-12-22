package ttit.com.shuvo.docdiary.hr_accounts.dashboards.arraylists;

public class OpasCardWiseErrorList {
    private String result_type;
    private String result_desc;
    private String failed_count;

    public OpasCardWiseErrorList(String result_type, String result_desc, String failed_count) {
        this.result_type = result_type;
        this.result_desc = result_desc;
        this.failed_count = failed_count;
    }

    public String getResult_type() {
        return result_type;
    }

    public void setResult_type(String result_type) {
        this.result_type = result_type;
    }

    public String getResult_desc() {
        return result_desc;
    }

    public void setResult_desc(String result_desc) {
        this.result_desc = result_desc;
    }

    public String getFailed_count() {
        return failed_count;
    }

    public void setFailed_count(String failed_count) {
        this.failed_count = failed_count;
    }
}
