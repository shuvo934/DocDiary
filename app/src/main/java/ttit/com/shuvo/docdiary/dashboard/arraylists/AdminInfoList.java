package ttit.com.shuvo.docdiary.dashboard.arraylists;

public class AdminInfoList {
    private String usr_id;
    private String usr_name;
    private String usr_fname;
    private String usr_lname;
    private String usr_email;
    private String usr_contact;
    private String all_access_flag;
    private String admin_center_name;

    public AdminInfoList(String usr_id, String usr_name, String usr_fname, String usr_lname, String usr_email, String usr_contact, String all_access_flag, String admin_center_name) {
        this.usr_id = usr_id;
        this.usr_name = usr_name;
        this.usr_fname = usr_fname;
        this.usr_lname = usr_lname;
        this.usr_email = usr_email;
        this.usr_contact = usr_contact;
        this.all_access_flag = all_access_flag;
        this.admin_center_name = admin_center_name;
    }

    public String getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(String usr_id) {
        this.usr_id = usr_id;
    }

    public String getUsr_name() {
        return usr_name;
    }

    public void setUsr_name(String usr_name) {
        this.usr_name = usr_name;
    }

    public String getUsr_fname() {
        return usr_fname;
    }

    public void setUsr_fname(String usr_fname) {
        this.usr_fname = usr_fname;
    }

    public String getUsr_lname() {
        return usr_lname;
    }

    public void setUsr_lname(String usr_lname) {
        this.usr_lname = usr_lname;
    }

    public String getUsr_email() {
        return usr_email;
    }

    public void setUsr_email(String usr_email) {
        this.usr_email = usr_email;
    }

    public String getUsr_contact() {
        return usr_contact;
    }

    public void setUsr_contact(String usr_contact) {
        this.usr_contact = usr_contact;
    }

    public String getAll_access_flag() {
        return all_access_flag;
    }

    public void setAll_access_flag(String all_access_flag) {
        this.all_access_flag = all_access_flag;
    }

    public String getAdmin_center_name() {
        return admin_center_name;
    }

    public void setAdmin_center_name(String admin_center_name) {
        this.admin_center_name = admin_center_name;
    }
}
