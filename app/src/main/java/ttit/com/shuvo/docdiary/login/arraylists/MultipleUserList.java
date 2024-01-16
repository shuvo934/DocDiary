package ttit.com.shuvo.docdiary.login.arraylists;

public class MultipleUserList {
    private String doc_code;
    private String doc_name;
    private String depts_name;


    public MultipleUserList(String doc_code, String doc_name, String depts_name) {
        this.doc_code = doc_code;
        this.doc_name = doc_name;
        this.depts_name = depts_name;
    }

    public String getDoc_code() {
        return doc_code;
    }

    public void setDoc_code(String doc_code) {
        this.doc_code = doc_code;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getDepts_name() {
        return depts_name;
    }

    public void setDepts_name(String depts_name) {
        this.depts_name = depts_name;
    }
}
