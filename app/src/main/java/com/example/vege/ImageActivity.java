package com.example.vege;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageActivity extends AppCompatActivity {

    final private static String TAG = "tag";
    private static final int CAMERA = 100;
    private static final int GALLERY = 101;

    TessBaseAPI tessBaseAPI;
    String dataPath = "";
    String langData = "kor";

    Button btn_camera;
    Button btn_gallery;
    Button btn_OCR;
    ImageView imageView;
    Intent intent;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat imageDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String imagePath;

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.iv_main);
        btn_camera = findViewById(R.id.btn_camera);
        btn_gallery = findViewById(R.id.btn_gallery);
        btn_OCR = findViewById(R.id.btn_OCR);

        try {
            dataPath = getFilesDir() + "/tesseract/";
            checkFile(new File(dataPath + "tessdata/"), "kor");
//            checkFile(new File(dataPath + "tessdata/"), "eng");

            tessBaseAPI = new TessBaseAPI();
            tessBaseAPI.init(dataPath, langData);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "?????? ?????? ??????");
            } else {
                Log.d(TAG, "?????? ?????? ??????");
                ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        btn_OCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOcrConvert(imageView);
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_camera:

                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA);
                        break;
                }
            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY);
            }
        });
    }

    public void checkFile(File dir, String lang){
        Log.d("---","---");
        Log.d("//===========//","================================================");
        Log.d("","\n"+"[A_CameraOcr > checkFile() ????????? : OCR ?????? ?????? ?????? ?????? ?????? ??????]");
        Log.d("","\n"+"[?????? ?????? : "+String.valueOf(lang)+"]");
        Log.d("//===========//","================================================");
        Log.d("---","---");
        try {
            if(!dir.exists() && dir.mkdirs()){
                copyFiles(lang);
            }

            if(dir.exists()){
                String dataFilePath = dataPath + "/tessdata/" + lang + ".traineddata";
                File dataFile = new File(dataFilePath);
                if(!dataFile.exists()){
                    copyFiles(lang);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void copyFiles(String lang){
        Log.d("---","---");
        Log.d("//===========//","================================================");
        Log.d("","\n"+"[A_CameraOcr > copyFiles() ????????? : OCR ?????? ?????? ?????? ?????? ?????? ??????]");
        Log.d("","\n"+"[?????? ?????? : "+String.valueOf(lang)+"]");
        Log.d("//===========//","================================================");
        Log.d("---","---");
        try {
            String filePath = dataPath + "/tessdata/" + lang + ".traineddata";

            AssetManager assetManager = getAssets();

            InputStream inputStream = assetManager.open("tessdata/" + lang + ".traineddata");
            OutputStream outputStream = new FileOutputStream(filePath);

            byte[] buffer = new byte[1024];
            int read;
            while((read = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getOcrConvert(ImageView image){
        Log.d("---","---");
        Log.d("//===========//","================================================");
        Log.d("","\n"+"[A_CameraOcr > getOcrConvert() ????????? : OCR ?????? ?????? ??????]");
        Log.d("//===========//","================================================");
        Log.d("---","---");
        try {
            //TODO [drawable ???????????? ??????????????? ????????????]
            //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_resource);

            //TODO [????????? ?????? ???????????? ??????????????? ????????????]
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

            //TODO [OCR ?????? ??????]
            tessBaseAPI.setImage(bitmap);
//            String result = String.valueOf(tessBaseAPI.getUTF8Text());
            String str = String.valueOf(tessBaseAPI.getUTF8Text());
            String target = "????????????";
            int target_num = str.indexOf(target);
            String result = str.substring(target_num, (str.substring(target_num).indexOf("??????") + target_num));

//            String[] array = result.split(",");
//
//            for (int i=0; i<array.length; i++) {
//                System.out.println(array[i]);
//            }

            //TODO [Alert ????????? ?????? ??????]
            getAlertDialog("[OCR] ?????? ?????? ??????",
                    String.valueOf(result),
                    "??????", "", "");

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getAlertDialog(String header, String content, String ok, String no, String normal){

        //TODO ????????? ??? ?????? ??????
        final String Tittle = header;
        final String Message = content;

        //TODO ?????? ?????? ??????
        String buttonNo = no;
        String buttonYes = ok;
        String buttonNature = normal;

        //TODO AlertDialog ????????? ??????
        new AlertDialog.Builder(ImageActivity.this)
                .setTitle(Tittle) //[????????? ????????? ??????]
                //.setIcon(R.drawable.tk_app_icon) //[????????? ????????? ??????]
                .setMessage(Message) //[????????? ?????? ??????]
                .setCancelable(false) //[?????? ???????????? ???????????? ???????????? ?????????????????? ??????]
                .setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .setNeutralButton(buttonNature, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
        else if(requestCode == GALLERY && resultCode == RESULT_OK) {
            Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
            if (cursor != null) {

                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                imagePath = cursor.getString(index);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                cursor.close();
            }
//                    InputStream ?????? ????????? ????????????
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                imageView.setImageBitmap(bitmap);
                Glide.with(this).load(imagePath).into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}