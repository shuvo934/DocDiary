package ttit.com.shuvo.docdiary.dashboard.arraylists;

public class PaymentChartList {
    private String id;
    private String dateMonth;
    private String paymentAmount;
    private String patymentReturn;

    public PaymentChartList(String id, String dateMonth, String paymentAmount, String patymentReturn) {
        this.id = id;
        this.dateMonth = dateMonth;
        this.paymentAmount = paymentAmount;
        this.patymentReturn = patymentReturn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateMonth() {
        return dateMonth;
    }

    public void setDateMonth(String dateMonth) {
        this.dateMonth = dateMonth;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPatymentReturn() {
        return patymentReturn;
    }

    public void setPatymentReturn(String patymentReturn) {
        this.patymentReturn = patymentReturn;
    }
}
