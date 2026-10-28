package ttit.com.shuvo.docdiary.dynamic_prescription.model;

public class VitalData {

    private final String systolic;
    private final String diastolic;
    private final String pulse;
    private final String temperature;
    private final String spo2;
    private final String respiratoryRate;
    private final String height;
    private final String weight;
    private final String bmi;

    public VitalData(String systolic, String diastolic, String pulse, String temperature, String spo2, String respiratoryRate, String height, String weight, String bmi) {
        this.systolic = clean(systolic);
        this.diastolic = clean(diastolic);
        this.pulse = clean(pulse);
        this.temperature = clean(temperature);
        this.spo2 = clean(spo2);
        this.respiratoryRate = clean(respiratoryRate);
        this.height = clean(height);
        this.weight = clean(weight);
        this.bmi = clean(bmi);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public String getSystolic() {
        return systolic;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public String getPulse() {
        return pulse;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getSpo2() {
        return spo2;
    }

    public String getRespiratoryRate() {
        return respiratoryRate;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getBmi() {
        return bmi;
    }

    public boolean isEmpty() {
        return systolic.isEmpty()
                && diastolic.isEmpty()
                && pulse.isEmpty()
                && temperature.isEmpty()
                && spo2.isEmpty()
                && respiratoryRate.isEmpty()
                && height.isEmpty()
                && weight.isEmpty();
    }
}
