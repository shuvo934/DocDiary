package ttit.com.shuvo.docdiary.report_manager.report_view.reports.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PdfRendererHelper {

    private final PdfRenderer renderer;

    public PdfRendererHelper(Context context, File pdfFile) {
        try {
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            renderer = new PdfRenderer(fd);
        } catch (IOException e) {
            throw new RuntimeException("PDF open failed", e);
        }
    }

    public int getPageCount() {
        return renderer.getPageCount();
    }

    public Bitmap renderPage(int pageIndex, float scale) {
        PdfRenderer.Page page = renderer.openPage(pageIndex);

        int width = (int) (page.getWidth() * scale);
        int height = (int) (page.getHeight() * scale);
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Rect rect = new Rect(0, 0, width, height);
        page.render(bmp, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close();

        return enhanceBitmap(bmp);
    }

    public void close() {
        renderer.close();
    }

    private Bitmap enhanceBitmap(Bitmap src) {
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Objects.requireNonNull(src.getConfig()));
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();

        ColorMatrix matrix = new ColorMatrix();
        float contrast = 1.4f;
        float brightness = 40f;

        matrix.set(new float[]{
                contrast, 0, 0, 0, brightness,
                0, contrast, 0, 0, brightness,
                0, 0, contrast, 0, brightness,
                0, 0, 0, 1, 0
        });

        paint.setColorFilter(new ColorMatrixColorFilter(matrix));
        canvas.drawBitmap(src, 0, 0, paint);
        return result;
    }
}
