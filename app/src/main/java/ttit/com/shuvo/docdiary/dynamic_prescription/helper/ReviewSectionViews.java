package ttit.com.shuvo.docdiary.dynamic_prescription.helper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import ttit.com.shuvo.docdiary.R;

public class ReviewSectionViews {

    public final ViewGroup root;

    public final AppCompatImageView ivIcon;

    public final TextView tvTitle;
    public final TextView tvBadge;
    public final TextView tvEmpty;

    public final MaterialCardView btnEdit;

    public final RecyclerView recyclerView;

    public ReviewSectionViews(@NonNull View sectionRoot) {
        root = (ViewGroup) sectionRoot;
        ivIcon = sectionRoot.findViewById(R.id.iv_review_section_icon);
        tvTitle = sectionRoot.findViewById(R.id.tv_review_section_title);
        tvBadge = sectionRoot.findViewById(R.id.tv_review_section_badge);
        tvEmpty = sectionRoot.findViewById(R.id.tv_review_section_empty);
        btnEdit = sectionRoot.findViewById(R.id.btn_review_section_edit);
        recyclerView = sectionRoot.findViewById(R.id.rv_review_section_rows);
    }
}