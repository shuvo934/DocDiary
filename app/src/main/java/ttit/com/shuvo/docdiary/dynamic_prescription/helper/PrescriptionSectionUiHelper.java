package ttit.com.shuvo.docdiary.dynamic_prescription.helper;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import ttit.com.shuvo.docdiary.R;

public class PrescriptionSectionUiHelper {
    private static final long ANIMATION_DURATION = 180L;

    private PrescriptionSectionUiHelper() {
        // Utility class
    }
    public static void setupSection(@NonNull SectionViews section, @NonNull String title, @DrawableRes int iconResource, @NonNull String emptyMessage, boolean initiallyExpanded, @Nullable View.OnClickListener addClickListener) {
        section.tvTitle.setText(title);
        section.ivIcon.setImageResource(iconResource);
        section.tvEmpty.setText(emptyMessage);
        section.tvCount.setText("0");

        if (section.recyclerView.getLayoutManager() == null) {
            section.recyclerView.setLayoutManager(new LinearLayoutManager(section.recyclerView.getContext()));
        }

        section.recyclerView.setNestedScrollingEnabled(false);
        section.recyclerView.setHasFixedSize(false);

        updateItemCount(section, 0);
        setExpanded(section, initiallyExpanded, false);

        View.OnClickListener expandClickListener = view -> toggleSection(section);

        section.headerLayout.setOnClickListener(expandClickListener);

        section.ivExpand.setOnClickListener(expandClickListener);

        section.btnAdd.setOnClickListener(view -> {
            if (section.contentLayout.getVisibility() != View.VISIBLE) {
                setExpanded(section, true, true);
            }

            if (addClickListener != null) {
                addClickListener.onClick(view);
            }
        });
    }

    public static void toggleSection(@NonNull SectionViews section) {
        boolean shouldExpand = section.contentLayout.getVisibility() != View.VISIBLE;
        setExpanded(section, shouldExpand, true);
    }

    public static void setExpanded(@NonNull SectionViews section, boolean expanded, boolean animate) {
        boolean currentlyExpanded = section.contentLayout.getVisibility() == View.VISIBLE;

        if (currentlyExpanded == expanded) {
            updateExpandIcon(section, expanded);
            return;
        }

        if (animate) {
            TransitionManager.beginDelayedTransition(section.root, new AutoTransition().setDuration(ANIMATION_DURATION));
        }

        section.contentLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
        updateExpandIcon(section, expanded);
    }

    public static void updateItemCount(@NonNull SectionViews section, int itemCount) {
        section.tvCount.setText(String.valueOf(itemCount));
        boolean hasItems = itemCount > 0;
        section.tvEmpty.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        section.recyclerView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
    }

    public static void showEmptyState(@NonNull SectionViews section) {
        updateItemCount(section, 0);
    }

    private static void updateExpandIcon(@NonNull SectionViews section, boolean expanded) {
        section.ivExpand.setImageResource(
                expanded ? R.drawable.ic_expand_less_24 : R.drawable.ic_expand_more_24);
    }
}
