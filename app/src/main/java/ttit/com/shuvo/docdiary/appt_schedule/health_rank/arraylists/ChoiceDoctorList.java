package ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists;

public class ChoiceDoctorList {
    private String pph_doc_id;
    private String doc_name;

    public ChoiceDoctorList(String pph_doc_id, String doc_name) {
        this.pph_doc_id = pph_doc_id;
        this.doc_name = doc_name;
    }

    public String getPph_doc_id() {
        return pph_doc_id;
    }

    public void setPph_doc_id(String pph_doc_id) {
        this.pph_doc_id = pph_doc_id;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }
}
