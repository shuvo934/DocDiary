package ttit.com.shuvo.docdiary.utilities;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.ComponentActivity;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EdgeToEdgeHelper {

    private EdgeToEdgeHelper() {}
    /**
     * Call before setContentView().
     */
    public static void enable(@NonNull ComponentActivity activity) {
        EdgeToEdge.enable(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.getWindow()
                    .setNavigationBarContrastEnforced(false);
        }
    }

    public static void applyInsets(
            @NonNull ComponentActivity activity,
            @NonNull View rootView,
            boolean darkStatusBarIcons,
            boolean darkNavigationBarIcons
    ) {

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(
                        activity.getWindow(),
                        activity.getWindow().getDecorView()
                );

        controller.setAppearanceLightStatusBars(darkStatusBarIcons);
        controller.setAppearanceLightNavigationBars(
                darkNavigationBarIcons
        );

        final int initialLeft = rootView.getPaddingLeft();
        final int initialTop = rootView.getPaddingTop();
        final int initialRight = rootView.getPaddingRight();
        final int initialBottom = rootView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(
                rootView,
                (view, windowInsets) -> {

                    int insetTypes =
                            WindowInsetsCompat.Type.systemBars()
                                    | WindowInsetsCompat.Type.displayCutout();

                    Insets safeInsets =
                            windowInsets.getInsets(insetTypes);

                    Insets imeInsets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.ime()
                    );

                    int requiredBottomInset = Math.max(
                            safeInsets.bottom,
                            imeInsets.bottom
                    );

                    view.setPadding(
                            initialLeft + safeInsets.left,
                            initialTop + safeInsets.top,
                            initialRight + safeInsets.right,
                            initialBottom + requiredBottomInset
                    );

                    return windowInsets;
                }
        );

        ViewCompat.requestApplyInsets(rootView);
    }

    public static void applyWithBottomNavigation(
            @NonNull ComponentActivity activity,
            @NonNull View rootView,
            @NonNull BottomNavigationView bottomNavigationView,
            @NonNull View systemNavigationBackground,
            boolean darkStatusBarIcons,
            boolean darkNavigationBarIcons
    ) {

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(
                        activity.getWindow(),
                        activity.getWindow().getDecorView()
                );

        controller.setAppearanceLightStatusBars(darkStatusBarIcons);
        controller.setAppearanceLightNavigationBars(
                darkNavigationBarIcons
        );

        /*
         * Save original root padding.
         */
        final int rootLeft = rootView.getPaddingLeft();
        final int rootTop = rootView.getPaddingTop();
        final int rootRight = rootView.getPaddingRight();
        final int rootBottom = rootView.getPaddingBottom();

        /*
         * Root handles all system-bar insets.
         */
        ViewCompat.setOnApplyWindowInsetsListener(
                rootView,
                (view, insets) -> {

                    Insets safeInsets = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                                    | WindowInsetsCompat.Type.displayCutout()
                    );

                    view.setPadding(
                            rootLeft + safeInsets.left,
                            rootTop + safeInsets.top,
                            rootRight + safeInsets.right,
                            rootBottom
                    );

                    /*
                     * Give the background View exactly the same height
                     * as the current system navigation bar.
                     */
                    Insets navigationInsets = insets.getInsets(
                            WindowInsetsCompat.Type.navigationBars()
                    );
                    System.out.println(navigationInsets.bottom);

                    ViewGroup.LayoutParams layoutParams =
                            systemNavigationBackground.getLayoutParams();

                    if (layoutParams.height != navigationInsets.bottom) {
                        layoutParams.height = navigationInsets.bottom;
                        systemNavigationBackground.setLayoutParams(layoutParams);
                    }

                    return insets;
                }
        );

        /*
         * Save original BottomNavigationView padding.
         */
        final int navStart =
                bottomNavigationView.getPaddingStart();

        final int navTop =
                bottomNavigationView.getPaddingTop();

        final int navEnd =
                bottomNavigationView.getPaddingEnd();

        final int navBottom =
                bottomNavigationView.getPaddingBottom();

        /*
         * Replace Material's automatic inset listener.
         *
         * Do not add navigationBars.bottom here,
         * because rootView already handles it.
         */
        ViewCompat.setOnApplyWindowInsetsListener(
                bottomNavigationView,
                (view, insets) -> {

                    view.setPaddingRelative(
                            navStart,
                            navTop,
                            navEnd,
                            navBottom + 10
                    );

                    return insets;
                }
        );

        ViewCompat.requestApplyInsets(rootView);
        ViewCompat.requestApplyInsets(bottomNavigationView);
    }
}
