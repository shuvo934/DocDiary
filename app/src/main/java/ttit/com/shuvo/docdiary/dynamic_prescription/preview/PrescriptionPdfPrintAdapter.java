package ttit.com.shuvo.docdiary.dynamic_prescription.preview;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PrescriptionPdfPrintAdapter extends PrintDocumentAdapter {

    @NonNull
    private final File prescriptionPdf;

    @NonNull
    private final String documentName;

    public PrescriptionPdfPrintAdapter(@NonNull File prescriptionPdf, @NonNull String documentName) {
        this.prescriptionPdf = prescriptionPdf;
        this.documentName = documentName;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (!prescriptionPdf.exists() || prescriptionPdf.length() <= 0) {
            callback.onLayoutFailed("Prescription PDF was not found.");
            return;
        }

        PrintDocumentInfo documentInfo = new PrintDocumentInfo.Builder(documentName + ".pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build();

        boolean layoutChanged = !newAttributes.equals(oldAttributes);

        callback.onLayoutFinished(documentInfo, layoutChanged);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        try (FileInputStream inputStream = new FileInputStream(prescriptionPdf);
             FileOutputStream outputStream = new FileOutputStream(destination.getFileDescriptor())) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    return;
                }

                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();

            callback.onWriteFinished(new PageRange[]{
                    PageRange.ALL_PAGES
            });

        } catch (Exception exception) {
            String errorMessage = exception.getMessage();

            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                errorMessage = "Prescription PDF could not be prepared for printing.";
            }

            callback.onWriteFailed(errorMessage);
        }
    }
}