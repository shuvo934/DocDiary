package ttit.com.shuvo.docdiary.hr_accounts.attendance.araylists;


public class AttendanceReportList {
    private String date;
    private String status;
    private String shift;
    private String punchLoc;
    private String inTime;
    private String inStatus;
    private String outTime;
    private String outStatus;
    private String attStatusColor;
    private String inLat;
    private String inLon;
    private String outLat;
    private String outLon;

    public AttendanceReportList(String date, String status, String shift, String punchLoc, String inTime, String inStatus, String outTime, String outStatus, String attStatusColor, String inLat, String inLon, String outLat, String outLon) {
        this.date = date;
        this.status = status;
        this.shift = shift;
        this.punchLoc = punchLoc;
        this.inTime = inTime;
        this.inStatus = inStatus;
        this.outTime = outTime;
        this.outStatus = outStatus;
        this.attStatusColor = attStatusColor;
        this.inLat = inLat;
        this.inLon = inLon;
        this.outLat = outLat;
        this.outLon = outLon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getPunchLoc() {
        return punchLoc;
    }

    public void setPunchLoc(String punchLoc) {
        this.punchLoc = punchLoc;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getInStatus() {
        return inStatus;
    }

    public void setInStatus(String inStatus) {
        this.inStatus = inStatus;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getOutStatus() {
        return outStatus;
    }

    public void setOutStatus(String outStatus) {
        this.outStatus = outStatus;
    }

    public String getAttStatusColor() {
        return attStatusColor;
    }

    public void setAttStatusColor(String attStatusColor) {
        this.attStatusColor = attStatusColor;
    }

    public String getInLat() {
        return inLat;
    }

    public void setInLat(String inLat) {
        this.inLat = inLat;
    }

    public String getInLon() {
        return inLon;
    }

    public void setInLon(String inLon) {
        this.inLon = inLon;
    }

    public String getOutLat() {
        return outLat;
    }

    public void setOutLat(String outLat) {
        this.outLat = outLat;
    }

    public String getOutLon() {
        return outLon;
    }

    public void setOutLon(String outLon) {
        this.outLon = outLon;
    }
}
