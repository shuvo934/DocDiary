package ttit.com.shuvo.docdiary.payment.interfaces;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.payment.arraylists.UpdatedPaymentMethodList;

public interface UpdateConfirmPayListener {
    void onUpdatePayConfirmation(ArrayList<UpdatedPaymentMethodList> selectedPaymentMethodLists);
}
