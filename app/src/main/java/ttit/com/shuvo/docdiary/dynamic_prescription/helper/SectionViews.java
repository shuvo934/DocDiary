package ttit.com.shuvo.docdiary.dynamic_prescription.helper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import ttit.com.shuvo.docdiary.R;

public class SectionViews {
    public final ViewGroup root;
    public final View headerLayout;
    public final View contentLayout;
    public final AppCompatImageView ivIcon;
    public final AppCompatImageView ivExpand;
    public final MaterialCardView btnAdd;

    public final TextView tvTitle;
    public final TextView tvCount;
    public final TextView tvEmpty;

    public final RecyclerView recyclerView;

    public SectionViews(@NonNull View sectionRoot) {

        root = (ViewGroup) sectionRoot;
        headerLayout = sectionRoot.findViewById(R.id.layout_section_header);
        contentLayout = sectionRoot.findViewById(R.id.layout_section_content);
        ivIcon = sectionRoot.findViewById(R.id.iv_section_icon);
        ivExpand = sectionRoot.findViewById(R.id.iv_section_expand);
        btnAdd = sectionRoot.findViewById(R.id.btn_section_add);
        tvTitle = sectionRoot.findViewById(R.id.tv_section_title);
        tvCount = sectionRoot.findViewById(R.id.tv_section_count);
        tvEmpty = sectionRoot.findViewById(R.id.tv_section_empty);
        recyclerView = sectionRoot.findViewById(R.id.rv_section_items);
    }
}
