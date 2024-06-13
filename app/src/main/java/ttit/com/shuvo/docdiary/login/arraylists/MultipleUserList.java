package ttit.com.shuvo.docdiary.login.arraylists;

public class MultipleUserList {
    private String doc_code;
    private String doc_name;
    private String depts_name;
    private String user_admin_flag;
    private String admin_user_id;
    private String admin_user_fname;
    private String admin_user_lname;
    private String admin_user_name;


    public MultipleUserList(String doc_code, String doc_name, String depts_name, String user_admin_flag, String admin_user_id, String admin_user_fname, String admin_user_lname, String admin_user_name) {
        this.doc_code = doc_code;
        this.doc_name = doc_name;
        this.depts_name = depts_name;
        this.user_admin_flag = user_admin_flag;
        this.admin_user_id = admin_user_id;
        this.admin_user_fname = admin_user_fname;
        this.admin_user_lname = admin_user_lname;
        this.admin_user_name = admin_user_name;
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

    public String getUser_admin_flag() {
        return user_admin_flag;
    }

    public void setUser_admin_flag(String user_admin_flag) {
        this.user_admin_flag = user_admin_flag;
    }

    public String getAdmin_user_id() {
        return admin_user_id;
    }

    public void setAdmin_user_id(String admin_user_id) {
        this.admin_user_id = admin_user_id;
    }

    public String getAdmin_user_fname() {
        return admin_user_fname;
    }

    public void setAdmin_user_fname(String admin_user_fname) {
        this.admin_user_fname = admin_user_fname;
    }

    public String getAdmin_user_lname() {
        return admin_user_lname;
    }

    public void setAdmin_user_lname(String admin_user_lname) {
        this.admin_user_lname = admin_user_lname;
    }

    public String getAdmin_user_name() {
        return admin_user_name;
    }

    public void setAdmin_user_name(String admin_user_name) {
        this.admin_user_name = admin_user_name;
    }
}
