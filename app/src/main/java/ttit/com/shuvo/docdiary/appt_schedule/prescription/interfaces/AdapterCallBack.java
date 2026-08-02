package ttit.com.shuvo.docdiary.appt_schedule.prescription.interfaces;

public interface AdapterCallBack {
    void showLoadingScreen();

    void setBoolPositive();

    void setBoolNegative();

    void hideLoadingScreen();

    void onListChanged(String type, int size);
}
