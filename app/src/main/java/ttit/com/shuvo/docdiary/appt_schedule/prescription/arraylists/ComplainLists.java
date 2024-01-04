package ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists;

public class ComplainLists {
    private String pci_id;
    private String pci_pmm_id;
    private String cm_id;
    private String cm_name;
    private String pci_date;
    private String injury_id;
    private String injury_name;

    public ComplainLists(String pci_id, String pci_pmm_id, String cm_id, String cm_name, String pci_date, String injury_id, String injury_name) {
        this.pci_id = pci_id;
        this.pci_pmm_id = pci_pmm_id;
        this.cm_id = cm_id;
        this.cm_name = cm_name;
        this.pci_date = pci_date;
        this.injury_id = injury_id;
        this.injury_name = injury_name;
    }

    public String getPci_id() {
        return pci_id;
    }

    public void setPci_id(String pci_id) {
        this.pci_id = pci_id;
    }

    public String getPci_pmm_id() {
        return pci_pmm_id;
    }

    public void setPci_pmm_id(String pci_pmm_id) {
        this.pci_pmm_id = pci_pmm_id;
    }

    public String getCm_id() {
        return cm_id;
    }

    public void setCm_id(String cm_id) {
        this.cm_id = cm_id;
    }

    public String getCm_name() {
        return cm_name;
    }

    public void setCm_name(String cm_name) {
        this.cm_name = cm_name;
    }

    public String getPci_date() {
        return pci_date;
    }

    public void setPci_date(String pci_date) {
        this.pci_date = pci_date;
    }

    public String getInjury_id() {
        return injury_id;
    }

    public void setInjury_id(String injury_id) {
        this.injury_id = injury_id;
    }

    public String getInjury_name() {
        return injury_name;
    }

    public void setInjury_name(String injury_name) {
        this.injury_name = injury_name;
    }
}
