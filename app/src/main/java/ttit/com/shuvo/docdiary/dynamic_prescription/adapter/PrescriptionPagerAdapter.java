package ttit.com.shuvo.docdiary.dynamic_prescription.adapter;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.fragments.PrescriptionAdviceFragment;
import ttit.com.shuvo.docdiary.dynamic_prescription.fragments.PrescriptionClinicalFragment;
import ttit.com.shuvo.docdiary.dynamic_prescription.fragments.PrescriptionReviewFragment;
import ttit.com.shuvo.docdiary.dynamic_prescription.fragments.PrescriptionRxFragment;

public class PrescriptionPagerAdapter extends FragmentStateAdapter {

    public static final int PAGE_CLINICAL = 0;
    public static final int PAGE_RX = 1;
    public static final int PAGE_ADVICE = 2;
    public static final int PAGE_REVIEW = 3;

    private static final int TOTAL_PAGES = 4;

    public PrescriptionPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case PAGE_CLINICAL:
                return new PrescriptionClinicalFragment();
            case PAGE_RX:
                return new PrescriptionRxFragment();

            case PAGE_ADVICE:
                return new PrescriptionAdviceFragment();

            case PAGE_REVIEW:
                return new PrescriptionReviewFragment();

            default:
                return new PrescriptionClinicalFragment();
        }
    }

    @Override
    public int getItemCount() {
        return TOTAL_PAGES;
    }

    /**
     * Temporary fragment for unfinished tabs.
     * It will be removed when actual fragments are created.
     */
    public static class PrescriptionPlaceholderFragment extends Fragment {
        private static final String ARG_MESSAGE = "placeholder_message";
        public PrescriptionPlaceholderFragment() {
            // Required empty constructor
        }

        public static PrescriptionPlaceholderFragment newInstance(
                @NonNull String message
        ) {

            PrescriptionPlaceholderFragment fragment =
                    new PrescriptionPlaceholderFragment();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_MESSAGE, message);

            fragment.setArguments(bundle);

            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(
                @NonNull LayoutInflater inflater,
                @Nullable ViewGroup container,
                @Nullable Bundle savedInstanceState
        ) {

            TextView messageView = new TextView(requireContext());

            messageView.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    )
            );

            messageView.setGravity(Gravity.CENTER);
            messageView.setPadding(30, 30, 30, 30);

            messageView.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.prescription_text_secondary
                    )
            );

            messageView.setBackgroundColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                    )
            );

            messageView.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    13
            );

            String message = "Screen will be added next";

            if (getArguments() != null) {

                message = getArguments().getString(
                        ARG_MESSAGE,
                        message
                );
            }

            messageView.setText(message);

            return messageView;
        }
    }
}