package com.googlecloude.carteam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

public class Camera {
    static boolean isImage = false;

    public static void startCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "image.jpg");
        Uri outputFileUri = FileProvider.getUriForFile(MyContext.getContext(),MyContext.getContext().getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        MyContext.getContext().startActivityForResult(intent, 0);
    }

    public static Bitmap showImage(ImageView imageView){
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = 500.f / width;
        float scaleHeight = 500.f / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        //matrix.postRotate(270);

        Bitmap newbtm = Bitmap.createBitmap(bitmap, 0, 0,width, height, matrix, true);

        bitmap.recycle();

        imageView.setImageBitmap(newbtm);
        isImage = true;

        return newbtm;
    }
}
