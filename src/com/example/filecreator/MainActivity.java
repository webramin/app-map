package com.example.filecreator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText mapInputField;
    private Button insertButton;
    private Button generateButton;
    private Button selectFolderButton;
    private File selectedDirectory = null; // متغیر برای ذخیره پوشه انتخاب شده

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapInputField = findViewById(R.id.mapInputField);
        insertButton = findViewById(R.id.insertButton);
        generateButton = findViewById(R.id.generateButton);
        selectFolderButton = findViewById(R.id.selectFolderButton);

        // دکمه انتخاب پوشه
        selectFolderButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openDirectoryPicker();
				}
			});

        // دکمه تولید
        generateButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String mapInput = mapInputField.getText().toString();
					if (!mapInput.isEmpty() && selectedDirectory != null) {
						createStructure(mapInput, selectedDirectory);
					} else {
						Toast.makeText(MainActivity.this, "لطفا پوشه را انتخاب کنید.", Toast.LENGTH_SHORT).show();
					}
				}
			});
    }

    private void openDirectoryPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedDirectory = new File(uri.getPath());
                Toast.makeText(this, "پوشه انتخاب شد: " + selectedDirectory.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createStructure(String mapInput, File directory) {
        String[] paths = mapInput.split("\n");

        // مسیر اصلی برای ایجاد پوشه‌ها
        File mainDir = new File(directory, "almas-website");
        if (!mainDir.exists()) {
            mainDir.mkdir();
        }

        // پردازش هر مسیر
        for (String path : paths) {
            String cleanPath = path.trim();
            if (cleanPath.isEmpty()) continue;

            String[] parts = cleanPath.split("/");
            File currentDir = mainDir;

            for (int i = 0; i < parts.length - 1; i++) {
                currentDir = new File(currentDir, parts[i]);
                if (!currentDir.exists()) {
                    currentDir.mkdir();
                }
            }

            // ایجاد فایل
            File file = new File(currentDir, parts[parts.length - 1]);
            createFile(file);
        }

        Toast.makeText(this, "پوشه‌ها و فایل‌ها ایجاد شدند.", Toast.LENGTH_SHORT).show();
    }

    private void createFile(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            String content = "File created in: " + file.getAbsolutePath();
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
