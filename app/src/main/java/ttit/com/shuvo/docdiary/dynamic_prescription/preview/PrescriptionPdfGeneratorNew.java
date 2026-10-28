package ttit.com.shuvo.docdiary.dynamic_prescription.preview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public final class PrescriptionPdfGeneratorNew {

    /*
     * A4 page size in PostScript points।
     * 1 point = 1/72 inch।
     */
    private static final int PDF_PAGE_WIDTH = 595;
    private static final int PDF_PAGE_HEIGHT = 842;

    /*
     * Download/Share PDF-এর page image সর্বোচ্চ 150 DPI হবে।
     * A4 width 8.27 inch × 150 DPI = প্রায় 1240 pixel।
     */
    private static final int PDF_PAGE_BITMAP_WIDTH = 1240;

    /*
     * HTML/CSS অনুযায়ী A4 width প্রায় 794 CSS pixel।
     */
    private static final float A4_CSS_WIDTH = 793.70f;

    private static final long RENDER_DELAY_MILLISECONDS = 250L;
    private static final long FINAL_LAYOUT_DELAY_MILLISECONDS = 80L;

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    /*
     * PDF WebView-এর DOM থেকে Header, Content, Footer এবং
     * page-break safe element-এর position বের করবে।
     *
     * Visible preview-এর HTML পরিবর্তন হবে না।
     * শুধু off-screen PDF copy থেকে static page number সরবে।
     */
    private static final String PAGE_LAYOUT_SCRIPT =
            "(function(){"
                    + "function getRect(element){"
                    + "if(!element){return null;}"
                    + "var rect=element.getBoundingClientRect();"
                    + "return {top:rect.top+window.pageYOffset,bottom:rect.bottom+window.pageYOffset};"
                    + "}"
                    + "var footer=document.querySelector('.footer');"
                    + "if(footer){"
                    + "footer.innerHTML=footer.innerHTML.replace(/\\s*\\|\\s*Page\\s+\\d+\\s+of\\s+\\d+\\s*$/i,'');"
                    + "}"
                    + "var patientCard=document.querySelector('.patient-card');"
                    + "var vitals=document.querySelector('.vitals');"
                    + "var columns=document.querySelector('.columns');"
                    + "var bottomInfo=document.querySelector('.bottom-info');"
                    + "var contentStart=patientCard||vitals||columns||bottomInfo;"
                    + "var contentStartRect=getRect(contentStart);"
                    + "var bottomInfoRect=getRect(bottomInfo);"
                    + "var footerRect=getRect(footer);"
                    + "if(!contentStartRect||!bottomInfoRect||!footerRect){"
                    + "return JSON.stringify({error:'Required prescription layout was not found.'});"
                    + "}"
                    + "var result={"
                    + "contentTop:contentStartRect.top,"
                    + "contentBottom:bottomInfoRect.top,"
                    + "footerTop:bottomInfoRect.top,"
                    + "footerBottom:footerRect.bottom,"
                    + "documentHeight:Math.max(document.body.scrollHeight,document.documentElement.scrollHeight,footerRect.bottom),"
                    + "ranges:[]"
                    + "};"
                    + "function addRange(element){"
                    + "var itemRect=getRect(element);"
                    + "if(itemRect&&itemRect.bottom>itemRect.top){"
                    + "result.ranges.push({top:itemRect.top,bottom:itemRect.bottom});"
                    + "}"
                    + "}"
                    + "var atomicItems=document.querySelectorAll('.patient-card,.vitals,.section-item,.medicine-card,.special-alert');"
                    + "Array.prototype.forEach.call(atomicItems,function(element){addRange(element);});"
                    + "var sections=document.querySelectorAll('.section');"
                    + "Array.prototype.forEach.call(sections,function(section){"
                    + "var title=section.querySelector('.section-title');"
                    + "var firstItem=section.querySelector('.medicine-card,.section-item');"
                    + "var titleRect=getRect(title);"
                    + "var firstItemRect=getRect(firstItem);"
                    + "if(titleRect&&firstItemRect&&firstItemRect.bottom>titleRect.top){"
                    + "result.ranges.push({top:titleRect.top,bottom:firstItemRect.bottom});"
                    + "}"
                    + "});"
                    + "var rxTitle=document.querySelector('.rx-title');"
                    + "if(rxTitle&&rxTitle.parentElement){"
                    + "var firstRxSection=rxTitle.parentElement.querySelector('.section');"
                    + "var firstRxItem=firstRxSection?firstRxSection.querySelector('.medicine-card,.section-item'):null;"
                    + "var rxTitleRect=getRect(rxTitle);"
                    + "var firstRxItemRect=getRect(firstRxItem);"
                    + "if(rxTitleRect&&firstRxItemRect&&firstRxItemRect.bottom>rxTitleRect.top){"
                    + "result.ranges.push({top:rxTitleRect.top,bottom:firstRxItemRect.bottom});"
                    + "}"
                    + "}"
                    + "return JSON.stringify(result);"
                    + "})();";

    public interface PdfGenerationCallback {
        void onPdfGenerated(@NonNull File pdfFile);
        void onPdfGenerationFailed(@NonNull String errorMessage);
    }

    private PrescriptionPdfGeneratorNew() {
    }

    /**
     * Prescription HTML থেকে local A4 PDF তৈরি করবে।
     * এটি preview-তে ব্যবহৃত WebView পরিবর্তন করবে না।
     * PDF-এর জন্য আলাদা off-screen WebView তৈরি হবে।
     */
    public static void generatePdf(@NonNull Context context, @NonNull String prescriptionHtml, @NonNull File destinationFile, @NonNull PdfGenerationCallback callback) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("generatePdf must be called from the main thread.");
        }

        if (prescriptionHtml.trim().isEmpty()) {
            callback.onPdfGenerationFailed("Prescription HTML is empty.");
            return;
        }

        File parentDirectory = destinationFile.getParentFile();

        if (parentDirectory != null && !parentDirectory.exists() && !parentDirectory.mkdirs()) {
            callback.onPdfGenerationFailed("PDF directory could not be created.");
            return;
        }

        createPdfWebView(context, prescriptionHtml, destinationFile, callback);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private static void createPdfWebView(@NonNull Context context, @NonNull String prescriptionHtml, @NonNull File destinationFile, @NonNull PdfGenerationCallback callback) {
        WebView pdfWebView;

        try {
            pdfWebView = new WebView(context);

        } catch (Exception exception) {
            callback.onPdfGenerationFailed(getErrorMessage(exception, "PDF WebView could not be created."));
            return;
        }

        /*
         * PDF canvas-এ reliable drawing-এর জন্য
         * software layer ব্যবহার করা হচ্ছে।
         */
        pdfWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        pdfWebView.setBackgroundColor(Color.WHITE);
        pdfWebView.setVerticalScrollBarEnabled(false);
        pdfWebView.setHorizontalScrollBarEnabled(false);

        WebSettings webSettings = pdfWebView.getSettings();

        /*
         * শুধু local generated HTML-এর page position পড়ার জন্য
         * off-screen WebView-তে JavaScript enable করা হচ্ছে।
         */
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(false);
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowContentAccess(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(false);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setTextZoom(100);
        webSettings.setDefaultTextEncodingName("UTF-8");

        /*
         * PDF WebView-এর initial scale 100% থাকবে।
         */
        pdfWebView.setInitialScale(100);

        float density = context.getResources().getDisplayMetrics().density;
        int a4WidthPixels = Math.max(1, Math.round(A4_CSS_WIDTH * density));
        int approximateA4HeightPixels = Math.max(1, Math.round(a4WidthPixels * PDF_PAGE_HEIGHT / (float) PDF_PAGE_WIDTH));

        /*
         * HTML load করার আগেই A4 width অনুযায়ী
         * WebView-এর initial measurement দেওয়া হচ্ছে।
         */
        measureAndLayoutWebView(pdfWebView, a4WidthPixels, approximateA4HeightPixels);

        AtomicBoolean renderingStarted = new AtomicBoolean(false);

        pdfWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (!renderingStarted.compareAndSet(false, true)) {
                    return;
                }

                /*
                 * HTML, font এবং Base64 logo render হওয়ার জন্য
                 * অল্প সময় অপেক্ষা করা হচ্ছে।
                 */
                MAIN_HANDLER.postDelayed(() ->
                                readPageLayoutAndPreparePdf(
                                        view,
                                        a4WidthPixels,
                                        approximateA4HeightPixels,
                                        destinationFile,
                                        callback),
                        RENDER_DELAY_MILLISECONDS);
            }
        });

        String printableHtml = preparePrintableHtml(prescriptionHtml);

        pdfWebView.loadDataWithBaseURL(
                null,
                printableHtml,
                "text/html",
                "UTF-8",
                null
        );
    }

    private static void readPageLayoutAndPreparePdf(@NonNull WebView pdfWebView, int a4WidthPixels, int minimumA4HeightPixels, @NonNull File destinationFile, @NonNull PdfGenerationCallback callback) {
        try {
            pdfWebView.evaluateJavascript(PAGE_LAYOUT_SCRIPT, javascriptResult -> {
                try {
                    PageLayout pageLayout = parsePageLayout(javascriptResult);

                    prepareWebViewAndWritePdf(
                            pdfWebView,
                            a4WidthPixels,
                            minimumA4HeightPixels,
                            pageLayout,
                            destinationFile,
                            callback
                    );

                } catch (Exception exception) {
                    destroyWebView(pdfWebView);
                    callback.onPdfGenerationFailed(getErrorMessage(exception, "Prescription page layout could not be prepared."));
                }
            });

        } catch (Exception exception) {
            destroyWebView(pdfWebView);
            callback.onPdfGenerationFailed(getErrorMessage(exception, "Prescription page layout could not be prepared."));
        }
    }

    @NonNull
    private static PageLayout parsePageLayout(String javascriptResult) throws Exception {
        String resultValue = decodeJavascriptResult(javascriptResult);
        JSONObject layoutObject = new JSONObject(resultValue);
        String layoutError = layoutObject.optString("error", "");

        if (!layoutError.trim().isEmpty()) {
            throw new IllegalStateException(layoutError);
        }

        PageLayout pageLayout = new PageLayout(
                layoutObject.getDouble("contentTop"),
                layoutObject.getDouble("contentBottom"),
                layoutObject.getDouble("footerTop"),
                layoutObject.getDouble("footerBottom"),
                layoutObject.getDouble("documentHeight")
        );

        JSONArray ranges = layoutObject.optJSONArray("ranges");

        if (ranges != null) {
            for (int index = 0; index < ranges.length(); index++) {
                JSONObject rangeObject = ranges.getJSONObject(index);

                pageLayout.keepTogetherRanges.add(new KeepTogetherRange(
                        rangeObject.getDouble("top"),
                        rangeObject.getDouble("bottom")
                ));
            }
        }

        if (pageLayout.contentTop < 0
                || pageLayout.contentBottom <= pageLayout.contentTop
                || pageLayout.footerTop < pageLayout.contentBottom
                || pageLayout.footerBottom <= pageLayout.footerTop) {

            throw new IllegalStateException("Prescription page layout is invalid.");
        }

        return pageLayout;
    }

    @NonNull
    private static String decodeJavascriptResult(String javascriptResult) throws Exception {
        if (javascriptResult == null
                || javascriptResult.trim().isEmpty()
                || "null".equals(javascriptResult.trim())) {

            throw new IllegalStateException("Prescription page information was not returned.");
        }

        String cleanResult = javascriptResult.trim();

        if (cleanResult.startsWith("\"") && cleanResult.endsWith("\"")) {
            JSONArray decodedResult = new JSONArray("[" + cleanResult + "]");
            return decodedResult.getString(0);
        }

        return cleanResult;
    }

    private static void prepareWebViewAndWritePdf(@NonNull WebView pdfWebView, int a4WidthPixels, int minimumA4HeightPixels, @NonNull PageLayout pageLayout, @NonNull File destinationFile, @NonNull PdfGenerationCallback callback) {
        try {
            /*
             * WebView-এর width A4 CSS width অনুযায়ী measured।
             * তাই CSS pixel থেকে rendered pixel conversion
             * width ratio থেকেই পাওয়া যাবে।
             */
            float cssToRenderedPixelScale = a4WidthPixels / A4_CSS_WIDTH;
            double requiredDocumentHeightCss = Math.max(pdfWebView.getContentHeight(), pageLayout.documentHeight);

            int renderedContentHeight = Math.max(
                    minimumA4HeightPixels,
                    (int) Math.ceil(requiredDocumentHeightCss * cssToRenderedPixelScale)
            );

            /*
             * WebView-কে সম্পূর্ণ document height অনুযায়ী
             * layout করা হচ্ছে, যাতে নিচের content-ও PDF-এ আসে।
             */
            measureAndLayoutWebView(pdfWebView, a4WidthPixels, renderedContentHeight);

            RenderedPageLayout renderedPageLayout = pageLayout.toRenderedPixels(
                    cssToRenderedPixelScale,
                    renderedContentHeight
            );

            MAIN_HANDLER.postDelayed(() ->
                            writeWebViewToPdf(
                                    pdfWebView,
                                    a4WidthPixels,
                                    renderedContentHeight,
                                    renderedPageLayout,
                                    destinationFile,
                                    callback),
                    FINAL_LAYOUT_DELAY_MILLISECONDS);

        } catch (Exception exception) {
            destroyWebView(pdfWebView);
            callback.onPdfGenerationFailed(getErrorMessage(exception, "Prescription PDF preparation failed."));
        }
    }

    private static void writeWebViewToPdf(@NonNull WebView pdfWebView, int webViewWidthPixels, int webViewHeightPixels, @NonNull RenderedPageLayout pageLayout, @NonNull File destinationFile, @NonNull PdfGenerationCallback callback) {
        PdfDocument pdfDocument = new PdfDocument();
        ArrayList<Bitmap> generatedPageBitmaps = new ArrayList<>();

        try {
            if (pageLayout.contentBottom > webViewHeightPixels
                    || pageLayout.footerBottom > webViewHeightPixels) {

                throw new IllegalStateException("Prescription content measurement is invalid.");
            }

            /*
             * WebView width-কে PDF-এর A4 width-এ fit করার scale।
             */
            float pdfScale = PDF_PAGE_WIDTH / (float) webViewWidthPixels;

            /*
             * একটি A4 PDF page-এর height WebView pixel-এ।
             */
            float sourcePageHeightPixels = PDF_PAGE_HEIGHT / pdfScale;
            int headerHeightPixels = pageLayout.contentTop;
            int footerHeightPixels = pageLayout.footerBottom - pageLayout.footerTop;

            /*
             * Device density যত বেশি হোক, final PDF page bitmap
             * 150 DPI-এর বেশি হবে না। Low-density device-এ
             * unnecessary upscaling-ও করা হবে না।
             */
            int pageBitmapWidth = Math.min(
                    webViewWidthPixels,
                    PDF_PAGE_BITMAP_WIDTH
            );

            float pageBitmapScale = pageBitmapWidth / (float) webViewWidthPixels;

            int pageBitmapHeight = Math.max(
                    1,
                    Math.round(sourcePageHeightPixels * pageBitmapScale)
            );

            int availableContentHeightPixels = (int) Math.floor(
                    sourcePageHeightPixels - headerHeightPixels - footerHeightPixels
            );

            if (headerHeightPixels <= 0
                    || footerHeightPixels <= 0
                    || availableContentHeightPixels <= 0) {

                throw new IllegalStateException("Prescription header or footer is too large for an A4 page.");
            }

            List<PageSegment> pageSegments = createPageSegments(
                    pageLayout.contentTop,
                    pageLayout.contentBottom,
                    availableContentHeightPixels,
                    pageLayout.keepTogetherRanges
            );

            if (pageSegments.isEmpty()) {
                throw new IllegalStateException("Prescription content could not be divided into pages.");
            }

            int pageCount = pageSegments.size();

            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                        PDF_PAGE_WIDTH,
                        PDF_PAGE_HEIGHT,
                        pageIndex + 1
                ).create();

                PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                try {
                    Canvas pdfCanvas = page.getCanvas();
                    pdfCanvas.drawColor(Color.WHITE);

                    /*
                     * পুরো লম্বা WebView প্রতিটি PDF page-এ embed না করে
                     * শুধু current A4 page-এর Header, Content এবং Footer
                     * এই bitmap-এ render করা হবে।
                     */
                    Bitmap pageBitmap = Bitmap.createBitmap(
                            pageBitmapWidth,
                            pageBitmapHeight,
                            Bitmap.Config.ARGB_8888
                    );

                    generatedPageBitmaps.add(pageBitmap);

                    Canvas pageBitmapCanvas = new Canvas(pageBitmap);
                    pageBitmapCanvas.drawColor(Color.WHITE);

                    PageSegment pageSegment = pageSegments.get(pageIndex);

                    /*
                     * Facility header থেকে Department bar পর্যন্ত
                     * প্রতিটি PDF page-এর উপরে থাকবে।
                     */
                    drawWebViewRegion(
                            pdfWebView,
                            pageBitmapCanvas,
                            pageBitmapScale,
                            webViewWidthPixels,
                            0,
                            pageLayout.contentTop,
                            0
                    );

                    /*
                     * Current page-এর নির্ধারিত content অংশ।
                     */
                    drawWebViewRegion(
                            pdfWebView,
                            pageBitmapCanvas,
                            pageBitmapScale,
                            webViewWidthPixels,
                            pageSegment.start,
                            pageSegment.end,
                            pageLayout.contentTop
                    );

                    /*
                     * Reference, Signature এবং Footer
                     * প্রতিটি PDF page-এর নিচে থাকবে।
                     */
                    int footerDestinationTop = Math.round(sourcePageHeightPixels - footerHeightPixels);

                    drawWebViewRegion(
                            pdfWebView,
                            pageBitmapCanvas,
                            pageBitmapScale,
                            webViewWidthPixels,
                            pageLayout.footerTop,
                            pageLayout.footerBottom,
                            footerDestinationTop
                    );

                    /*
                     * Current page bitmap-কে A4 PDF canvas-এ fit করা হচ্ছে।
                     */
                    Paint pageBitmapPaint = new Paint(
                            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG
                    );

                    pdfCanvas.drawBitmap(
                            pageBitmap,
                            null,
                            new RectF(
                                    0f,
                                    0f,
                                    PDF_PAGE_WIDTH,
                                    PDF_PAGE_HEIGHT
                            ),
                            pageBitmapPaint
                    );

                    /*
                     * Page number vector text হিসেবেই থাকবে।
                     */
                    drawPageNumber(pdfCanvas, pageIndex + 1, pageCount);

                } finally {
                    pdfDocument.finishPage(page);
                }
            }

            try (FileOutputStream outputStream = new FileOutputStream(destinationFile, false)) {
                pdfDocument.writeTo(outputStream);
                outputStream.flush();
            }

            if (!destinationFile.exists() || destinationFile.length() <= 0) {
                throw new IllegalStateException("Generated PDF file is empty.");
            }

            destroyWebView(pdfWebView);
            callback.onPdfGenerated(destinationFile);

        } catch (Exception exception) {
            destroyWebView(pdfWebView);

            if (destinationFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                destinationFile.delete();
            }

            callback.onPdfGenerationFailed(getErrorMessage(exception, "Prescription PDF generation failed."));

        } finally {
            pdfDocument.close();

            for (Bitmap pageBitmap : generatedPageBitmaps) {
                if (!pageBitmap.isRecycled()) {
                    pageBitmap.recycle();
                }
            }

            generatedPageBitmaps.clear();
        }
    }

    @NonNull
    private static List<PageSegment> createPageSegments(int contentTop, int contentBottom, int availableContentHeight, @NonNull List<RenderedKeepTogetherRange> keepTogetherRanges) {
        ArrayList<PageSegment> pageSegments = new ArrayList<>();
        int currentPageStart = contentTop;
        int safetyCounter = 0;

        while (currentPageStart < contentBottom) {
            safetyCounter++;

            if (safetyCounter > 500) {
                throw new IllegalStateException("Prescription contains too many pages.");
            }

            int proposedPageEnd = Math.min(currentPageStart + availableContentHeight, contentBottom);

            int safePageEnd = findSafePageEnd(
                    currentPageStart,
                    proposedPageEnd,
                    contentBottom,
                    availableContentHeight,
                    keepTogetherRanges
            );

            if (safePageEnd <= currentPageStart) {
                safePageEnd = proposedPageEnd;
            }

            pageSegments.add(new PageSegment(currentPageStart, safePageEnd));
            currentPageStart = safePageEnd;
        }

        return pageSegments;
    }

    private static int findSafePageEnd(int currentPageStart, int proposedPageEnd, int contentBottom, int availableContentHeight, @NonNull List<RenderedKeepTogetherRange> keepTogetherRanges) {
        if (proposedPageEnd >= contentBottom) {
            return contentBottom;
        }

        int adjustedPageEnd = proposedPageEnd;
        boolean pageEndChanged;

        do {
            pageEndChanged = false;

            for (RenderedKeepTogetherRange range : keepTogetherRanges) {
                int rangeHeight = range.bottom - range.top;

                /*
                 * কোনো single element যদি পুরো available page-এর
                 * চেয়েও বড় হয়, সেটি বাধ্যতামূলকভাবে split হবে।
                 */
                if (rangeHeight <= 0 || rangeHeight > availableContentHeight) {
                    continue;
                }

                boolean pageBreakFallsInsideRange =
                        range.top > currentPageStart
                                && range.top < adjustedPageEnd
                                && range.bottom > adjustedPageEnd;

                if (pageBreakFallsInsideRange) {
                    adjustedPageEnd = range.top;
                    pageEndChanged = true;
                }
            }

        } while (pageEndChanged);

        return adjustedPageEnd;
    }

    private static void drawWebViewRegion(@NonNull WebView pdfWebView, @NonNull Canvas canvas, float pdfScale, int webViewWidthPixels, int sourceTop, int sourceBottom, int destinationTop) {
        if (sourceBottom <= sourceTop) {
            return;
        }

        int regionHeight = sourceBottom - sourceTop;

        canvas.save();
        canvas.scale(pdfScale, pdfScale);
        canvas.clipRect(
                0,
                destinationTop,
                webViewWidthPixels,
                destinationTop + regionHeight
        );
        canvas.translate(0, destinationTop - sourceTop);
        pdfWebView.draw(canvas);
        canvas.restore();
    }

    private static void drawPageNumber(@NonNull Canvas canvas, int pageNumber, int pageCount) {
        Paint pageNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pageNumberPaint.setColor(Color.WHITE);
        pageNumberPaint.setTextSize(6.5f);
        pageNumberPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        pageNumberPaint.setTextAlign(Paint.Align.RIGHT);

        canvas.drawText(
                String.format(Locale.US, "Page %d of %d", pageNumber, pageCount),
                PDF_PAGE_WIDTH - 28f,
                PDF_PAGE_HEIGHT - 10f,
                pageNumberPaint
        );
    }

    private static void measureAndLayoutWebView(@NonNull WebView webView, int widthPixels, int heightPixels) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(widthPixels, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(heightPixels, View.MeasureSpec.EXACTLY);

        webView.measure(widthMeasureSpec, heightMeasureSpec);
        webView.layout(0, 0, widthPixels, heightPixels);
    }

    /**
     * Builder-এর code পরিবর্তন না করে PDF rendering copy-তে
     * screen background, margin এবং shadow সরানো হচ্ছে।
     */
    @NonNull
    private static String preparePrintableHtml(@NonNull String prescriptionHtml) {
        String pdfOnlyStyle =
                "<style>"
                        + "html,body{"
                        + "margin:0!important;"
                        + "padding:0!important;"
                        + "width:210mm!important;"
                        + "background:#FFFFFF!important;"
                        + "overflow:visible!important;"
                        + "}"
                        + ".sheet{"
                        + "width:210mm!important;"
                        + "min-height:297mm!important;"
                        + "margin:0!important;"
                        + "box-shadow:none!important;"
                        + "}"
                        + "</style>";

        int headClosingPosition = prescriptionHtml.toLowerCase(Locale.US).lastIndexOf("</head>");

        if (headClosingPosition < 0) {
            return pdfOnlyStyle + prescriptionHtml;
        }

        return prescriptionHtml.substring(0, headClosingPosition)
                + pdfOnlyStyle
                + prescriptionHtml.substring(headClosingPosition);
    }

    /**
     * System Print dialog-এও একই A4 configuration
     * ব্যবহার করা হবে।
     */
    @NonNull
    public static PrintAttributes createA4PrintAttributes() {
        return new PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4.asPortrait())
                .setResolution(new PrintAttributes.Resolution(
                        "prescription_pdf",
                        "Prescription PDF",
                        300,
                        300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .build();
    }

    private static void destroyWebView(@NonNull WebView webView) {
        try {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.clearHistory();
            webView.removeAllViews();
            webView.destroy();

        } catch (Exception ignored) {
        }
    }

    @NonNull
    private static String getErrorMessage(@NonNull Exception exception, @NonNull String fallbackMessage) {
        if (exception.getMessage() == null || exception.getMessage().trim().isEmpty()) {
            return fallbackMessage;
        }

        return exception.getMessage().trim();
    }

    private static final class PageLayout {

        private final double contentTop;
        private final double contentBottom;
        private final double footerTop;
        private final double footerBottom;
        private final double documentHeight;

        @NonNull
        private final ArrayList<KeepTogetherRange> keepTogetherRanges = new ArrayList<>();

        private PageLayout(double contentTop, double contentBottom, double footerTop, double footerBottom, double documentHeight) {
            this.contentTop = contentTop;
            this.contentBottom = contentBottom;
            this.footerTop = footerTop;
            this.footerBottom = footerBottom;
            this.documentHeight = documentHeight;
        }

        @NonNull
        private RenderedPageLayout toRenderedPixels(float scale, int renderedDocumentHeight) {
            int renderedContentTop = toRenderedPixel(contentTop, scale);
            int renderedContentBottom = toRenderedPixel(contentBottom, scale);
            int renderedFooterTop = toRenderedPixel(footerTop, scale);
            int renderedFooterBottom = Math.min(
                    renderedDocumentHeight,
                    toRenderedPixel(footerBottom, scale)
            );

            ArrayList<RenderedKeepTogetherRange> renderedRanges = new ArrayList<>();

            for (KeepTogetherRange range : keepTogetherRanges) {
                int renderedTop = toRenderedPixel(range.top, scale);
                int renderedBottom = toRenderedPixel(range.bottom, scale);

                if (renderedBottom > renderedContentTop && renderedTop < renderedContentBottom) {
                    renderedRanges.add(new RenderedKeepTogetherRange(
                            Math.max(renderedContentTop, renderedTop),
                            Math.min(renderedContentBottom, renderedBottom)
                    ));
                }
            }

            return new RenderedPageLayout(
                    renderedContentTop,
                    renderedContentBottom,
                    renderedFooterTop,
                    renderedFooterBottom,
                    renderedRanges
            );
        }

        private int toRenderedPixel(double cssPixel, float scale) {
            return Math.max(0, (int) Math.round(cssPixel * scale));
        }
    }

    private static final class KeepTogetherRange {

        private final double top;
        private final double bottom;

        private KeepTogetherRange(double top, double bottom) {
            this.top = top;
            this.bottom = bottom;
        }
    }

    private static final class RenderedPageLayout {

        private final int contentTop;
        private final int contentBottom;
        private final int footerTop;
        private final int footerBottom;

        @NonNull
        private final ArrayList<RenderedKeepTogetherRange> keepTogetherRanges;

        private RenderedPageLayout(int contentTop, int contentBottom, int footerTop, int footerBottom, @NonNull ArrayList<RenderedKeepTogetherRange> keepTogetherRanges) {
            this.contentTop = contentTop;
            this.contentBottom = contentBottom;
            this.footerTop = footerTop;
            this.footerBottom = footerBottom;
            this.keepTogetherRanges = keepTogetherRanges;
        }
    }

    private static final class RenderedKeepTogetherRange {

        private final int top;
        private final int bottom;

        private RenderedKeepTogetherRange(int top, int bottom) {
            this.top = top;
            this.bottom = bottom;
        }
    }

    private static final class PageSegment {

        private final int start;
        private final int end;

        private PageSegment(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}