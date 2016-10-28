package com.partymaker.holms;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private Integer PERMISSION_REQUEST = 2;
    private TextView pathTV;
    private Button selectBTN;
    private Button skanBTN;
    private TextView resultTV;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        askForPhoneStatePermission();

        pathTV = (TextView) findViewById(R.id.pathTV);
        selectBTN = (Button) findViewById(R.id.selectBTN);
        skanBTN = (Button) findViewById(R.id.skanBTN);
        resultTV = (TextView) findViewById(R.id.resultTV);


        selectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(MainActivity.this)
                        .withRequestCode(1)
                        //.withFilter(Pattern.compile(".*\\.wav$")) // Filtering files and directories by file name using regexp
                        //.withFilterDirectories(true) // Set directories filterable (false by default)
                        //.withHiddenFiles(true) // Show hidden files and folders
                        .start();
            }
        });
        skanBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(path == null){
                    Toast.makeText(getApplicationContext(),"Сначала выберите файл!", Toast.LENGTH_LONG).show();
                }else{
                    File file = new File(path);
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        decode(fileInputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            pathTV.setText(path);
        }
    }



    public void decode(final InputStream inputStream) throws IOException {
        for(int i = 0; i < 44; i++){
            inputStream.read();
        }
        String letter = "";
        String text = "";
        Integer countA = 0;
        while (inputStream.available() > 0) {
            int value = inputStream.read() & 1;
            letter += value;
            if(letter.length() == 5){
                String result = String.format("%c", Integer.parseInt(letter, 2) + 'а');
                text += result;
                if(result.equals("а")){
                    countA++;
                }else{
                    countA = 0;
                }
                letter = "";
            }
            if(countA > 3){
                break;
            }
            inputStream.read();
        }
        resultTV.setText(text);
    }

    public void askForPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                    ) {

                // Should we show an explanation?
                if (
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)

                        ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("write external storage access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm write external storage");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_EXTERNAL_STORAGE}
                                    , PERMISSION_REQUEST);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* — don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {

            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
            return;
        }
    }
}
