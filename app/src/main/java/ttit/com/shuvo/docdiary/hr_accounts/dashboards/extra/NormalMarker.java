package ttit.com.shuvo.docdiary.hr_accounts.dashboards.extra;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import ttit.com.shuvo.docdiary.R;
public class NormalMarker extends MarkerView {

    private final TextView tvContent;
    public NormalMarker(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            String firstText = Utils.formatNumber(ce.getHigh(), 0, true, ',');
            tvContent.setText(firstText);
        } else {

            String firstText = Utils.formatNumber(e.getY(), 0, true,',');
            tvContent.setText(firstText);
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-((float) getWidth() / 2), -getHeight());
    }

}

