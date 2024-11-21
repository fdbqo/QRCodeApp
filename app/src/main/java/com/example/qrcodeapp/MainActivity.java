package com.example.qrcodeapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout titleInputLayout;
    private TextInputEditText titleEditText;
    private TextInputLayout websiteInputLayout;
    private TextInputEditText websiteEditText;
    private MaterialButton scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleInputLayout = findViewById(R.id.titleInputLayout);
        titleEditText = findViewById(R.id.titleEditText);
        websiteInputLayout = findViewById(R.id.websiteInputLayout);
        websiteEditText = findViewById(R.id.websiteEditText);
        scanButton = findViewById(R.id.scanButton);

        scanButton.setOnClickListener(v -> initiateScan());

        websiteEditText.setOnClickListener(v -> openWebsite());
    }

    private void initiateScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    private void openWebsite() {
        String url = websiteEditText.getText().toString();
        if (!url.isEmpty()) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scanning cancelled", Toast.LENGTH_LONG).show();
            } else {
                processScannedData(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processScannedData(String scannedData) {
        try {
            JSONObject jsonObject = new JSONObject(scannedData);
            String title = jsonObject.optString("title", "");
            String website = jsonObject.optString("website", "");

            if (!title.isEmpty() || !website.isEmpty()) {
                titleEditText.setText(title);
                websiteEditText.setText(website);
                Toast.makeText(this, "QR Code scanned successfully", Toast.LENGTH_LONG).show();
            } else {
                throw new JSONException("Empty title and website");
            }
        } catch (JSONException e) {
            titleEditText.setText("Scanned URL");
            websiteEditText.setText(scannedData);
            Toast.makeText(this, "Non-JSON QR Code scanned", Toast.LENGTH_LONG).show();
        }
    }
}
