package ttit.com.shuvo.docdiary.patient_search.arraylists;

public class PatientSearchDypList {
    private final String pat_id;
    private final String pat_name;
    private final String ph_id;
    private final String sub_code;
    private final String dd_thana_name;
    private final String pph_progress;
    private final String pyear;
    private final String pat_cell;
    private final String gender;
    private final String pat_age;

    public PatientSearchDypList(String pat_id, String pat_name, String ph_id, String sub_code, String dd_thana_name, String pph_progress, String pyear, String pat_cell, String gender, String pat_age) {
        this.pat_id = pat_id;
        this.pat_name = pat_name;
        this.ph_id = ph_id;
        this.sub_code = sub_code;
        this.dd_thana_name = dd_thana_name;
        this.pph_progress = pph_progress;
        this.pyear = pyear;
        this.pat_cell = pat_cell;
        this.gender = gender;
        this.pat_age = pat_age;
    }

    public String getPat_id() {
        return pat_id;
    }

    public String getPat_name() {
        return pat_name;
    }

    public String getPh_id() {
        return ph_id;
    }

    public String getSub_code() {
        return sub_code;
    }

    public String getDd_thana_name() {
        return dd_thana_name;
    }

    public String getPph_progress() {
        return pph_progress;
    }

    public String getPyear() {
        return pyear;
    }

    public String getPat_cell() {
        return pat_cell;
    }

    public String getGender() {
        return gender;
    }

    public String getPat_age() {
        return pat_age;
    }
}
