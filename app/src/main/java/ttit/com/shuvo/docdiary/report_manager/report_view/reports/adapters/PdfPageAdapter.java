package ttit.com.shuvo.docdiary.report_manager.report_view.reports.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.request.PdfRendererHelper;

public class PdfPageAdapter extends RecyclerView.Adapter<PdfPageAdapter.PdfViewHolder> {

    private final PdfRendererHelper helper;

    public PdfPageAdapter(PdfRendererHelper helper) {
        this.helper = helper;
    }

    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_page_view, parent, false);
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfViewHolder holder, int position) {
        float scale = 2.5f;
        holder.imageView.setImageBitmap(helper.renderPage(position, scale));
    }

    @Override
    public int getItemCount() {
        return helper.getPageCount();
    }

    public static class PdfViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        PdfViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
