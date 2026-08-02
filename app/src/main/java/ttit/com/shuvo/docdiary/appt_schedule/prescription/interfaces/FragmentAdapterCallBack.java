package ttit.com.shuvo.docdiary.appt_schedule.prescription.interfaces;

public interface FragmentAdapterCallBack {
    void showLoading();

    void setBoolPositive();

    void setBoolNegative();

    void hideLoading();

    void onListChanged(int size);
}
