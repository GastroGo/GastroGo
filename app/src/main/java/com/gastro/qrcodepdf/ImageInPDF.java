package com.gastro.qrcodepdf;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.util.Random;

public class ImageInPDF {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    public static File createPDFWithImages(Context context, Bitmap[] bitmaps) {
        try {
            // Überprüfen Sie die Berechtigung zum Schreiben in den externen Speicher
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }

            // Erstelle ein Dokument
            Document document = new Document(PageSize.A4);

            // Pfad zur PDF-Datei im externen Speicher
            String pdfFileName = generateRandomString();
            File pdfFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), pdfFileName);

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