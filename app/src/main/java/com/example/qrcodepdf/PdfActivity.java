package com.example.qrcodepdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;

public class PdfActivity extends AppCompatActivity {

    int anzahl = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        Slider slider = findViewById(R.id.slider);
        TextView sliderValue = findViewById(R.id.counter);

        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                anzahl = (int) value;
                sliderValue.setText(Integer.toString(anzahl));
            }
        });

        FloatingActionButton btnDownloadPDF = findViewById(R.id.btnDownloadPDF);
        btnDownloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadAndOpenPDF();
            }
        });
    }

    String id = "-NkF_dqyroONEdMqgfgC";

    public String[] mergingID(int count) {
        String[] idMerged = new String[count];
        for (int i = 0; i < count; i++) {
            idMerged[i] = id + String.format("%03d", i+1);
        }
        return idMerged;
    }

    public Bitmap[] gerneratingQRCode() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        int index = 0;
        try {
            String[] idMerged = mergingID(anzahl);
            Bitmap[] bitmaps = new Bitmap[anzahl];
            for (String mergedId : idMerged) {
                BitMatrix bitMatrix = multiFormatWriter.encode(mergedId, BarcodeFormat.QR_CODE, 320, 320);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                bitmaps[index] = bitmap;

                index++;
            }
            return bitmaps;
        } catch (WriterException e){
            throw new RuntimeException(e);
        }
    }

    public void downloadAndOpenPDF() {

        // Erzeuge die PDF-Datei
        File pdfFile = ImageInPDF.createPDFWithImages(this, gerneratingQRCode());

        if (pdfFile != null) {
            // Öffne die PDF-Datei mit einem Intent
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Fehler beim Erstellen der PDF-Datei", Toast.LENGTH_SHORT).show();
        }
    }
}