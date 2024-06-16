package com.gastro.bill;

import static com.gastro.qrcodepdf.ImageInPDF.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.gastro.database.Data;
import com.gastro.database.Dish;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BillPDFGenerator {

    String restaurantId, tableId;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;
    Map<String, Long> orders;
    Map<String, Dish> dishData;
    String[] dishIds;
    Data restaurantData;
    Calendar calendar = Calendar.getInstance();
    String date = String.format("%02d-%02d-%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    Context context;

    public BillPDFGenerator(String restaurantId, String tableId, Context context) {
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.dbRef = database.getReference("Restaurants/" + this.tableId);
        this.context = context;
    }

    public void saveBillPDF() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Restaurants/" + restaurantId);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                restaurantData = snapshot.child("daten").getValue(Data.class);
                dishData = new HashMap<>();
                for (DataSnapshot dishSnapshot : snapshot.child("speisekarte").getChildren()) {
                    Dish dish = dishSnapshot.getValue(Dish.class);
                    dishData.put(dishSnapshot.getKey(), dish);
                }
                Map<String, Long> openOrders = (Map<String, Long>) snapshot.child("tische/" + tableId + "/bestellungen").getValue();
                Map<String, Long> closedOrders = (Map<String, Long>) snapshot.child("tische/" + tableId + "/geschlosseneBestellungen").getValue();

                orders = new HashMap<>(openOrders);

                closedOrders.forEach((key, value) ->
                        orders.merge(key, value, Long::sum)
                );


                Log.i("o", orders + "");

                orders.values().removeIf(val -> val == 0);

                dishIds = orders.keySet().toArray(new String[0]);

                writePDF();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void writePDF() {
        String path = String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
        File file = new File(path, (restaurantData.getName() + ", " + date).replace(" ", ""));

        Document document = new Document();
        try {

            // Überprüfen Sie die Berechtigung zum Schreiben in den externen Speicher
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Add header
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Paragraph title = new Paragraph(restaurantData.getName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Font subFont = new Font(Font.FontFamily.HELVETICA, 10);
            Paragraph subTitle = new Paragraph(restaurantData.getStrasse() + " " + restaurantData.getHausnr() + ", \n" + restaurantData.getPlz() + " " + restaurantData.getOrt(), subFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subTitle);

            document.add(Chunk.NEWLINE);

            // Add Date only
            PdfPTable billInfoTable = new PdfPTable(1);
            billInfoTable.setWidthPercentage(100);
            billInfoTable.setSpacingBefore(10f);
            billInfoTable.setSpacingAfter(10f);

            PdfPCell dateCell = new PdfPCell(new Phrase("Date: " + date, subFont));
            dateCell.setBorder(PdfPCell.NO_BORDER);
            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            billInfoTable.addCell(dateCell);

            document.add(billInfoTable);

            document.add(Chunk.NEWLINE);

            // Add table for items
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

            table.addCell(new PdfPCell(new Phrase("Product", boldFont)));
            table.addCell(new PdfPCell(new Phrase("Quantity", boldFont)));
            table.addCell(new PdfPCell(new Phrase("Price", boldFont)));
            table.addCell(new PdfPCell(new Phrase("Line Total", boldFont)));

            double totalCost = 0;

            for (int i = 0; i < orders.size(); i++) {
                String dishId = dishIds[i];

                table.addCell(dishData.get(dishId).getGericht());
                table.addCell(orders.get(dishId).toString());
                table.addCell(dishData.get(dishId).getPreis().toString() + "€");
                double lineTotal =  dishData.get(dishId).getPreis() * orders.get(dishId);
                totalCost += lineTotal;
                table.addCell(String.valueOf(lineTotal) + "€");
            }

            document.add(table);

            document.add(Chunk.NEWLINE);

            // Add totals section
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(10f);
            summaryTable.setSpacingAfter(10f);

            summaryTable.addCell("");
            summaryTable.addCell("");

            PdfPCell totalCell = new PdfPCell(new Phrase("Total", boldFont));
            summaryTable.addCell(totalCell);
            summaryTable.addCell(String.valueOf(totalCost)  + "€");

            document.add(summaryTable);

            document.add(Chunk.NEWLINE);

            // Add footer note
            Paragraph footerNote = new Paragraph("Note: Prices are inclusive of all Govt. taxes\nThank You, Please do come again", subFont);
            footerNote.setAlignment(Element.ALIGN_CENTER);
            document.add(footerNote);

            document.close();

            openPDF(file);
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void openPDF(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
