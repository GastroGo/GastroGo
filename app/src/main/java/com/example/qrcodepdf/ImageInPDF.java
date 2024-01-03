package com.example.qrcodepdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import java.util.Random;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ImageInPDF {

    public static File createPDFWithImages(Context context, Bitmap[] bitmaps) {
        try {
            // Erstelle ein Dokument
            Document document = new Document(PageSize.A4);

            // Pfad zum PDF-Datei im internen Speicher
            String pdfFileName = generateRandomString();
            File pdfFile = new File(context.getFilesDir(), pdfFileName);

            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            PdfPTable table = new PdfPTable(2);  // 2 Bilder pro Zeile

            for (Bitmap bitmap : bitmaps) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());

                float scaleFactor = 0.6f; // Du kannst den Wert je nach Bedarf anpassen
                image.scaleToFit(image.getWidth() * scaleFactor, image.getHeight() * scaleFactor);

                PdfPCell cell = new PdfPCell(image);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setFixedHeight((float) (PageSize.A4.getHeight() / 3.6));  // 3 Zeilen pro Seite

                table.addCell(cell);
            }

            document.add(table);
            document.close();
            return pdfFile;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static String generateRandomString() {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            char randomChar = characters.charAt(random.nextInt(characters.length()));
            stringBuilder.append(randomChar);
        }
        stringBuilder.append(".pdf");
        return stringBuilder.toString();
    }
}
