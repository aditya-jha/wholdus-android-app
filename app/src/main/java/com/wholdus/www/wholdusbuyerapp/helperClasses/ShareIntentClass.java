package com.wholdus.www.wholdusbuyerapp.helperClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by aditya on 21/12/16.
 */

public final class ShareIntentClass {

    public static void shareImage(
            final Context context,
            final ImageView imageView,
            final String title) {

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                try {
                    Drawable drawable = imageView.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                    File cachePath = new File(context.getCacheDir(), "images");
                    if (!cachePath.isDirectory()) {
                        cachePath.mkdirs();
                    }

                    String filePath = cachePath + "/image.png";
                    FileOutputStream stream = new FileOutputStream(filePath); // overwrites this image every time
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                    Uri pathUri = FileProvider.getUriForFile(context, "com.wholdus.www.wholdusbuyerapp.fileprovider", new File(filePath));

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);

                    shareIntent.putExtra(Intent.EXTRA_TEXT, title);
                    shareIntent.setType("text/plain");

                    shareIntent.putExtra(Intent.EXTRA_STREAM, pathUri);
                    shareIntent.setType("image/*");

                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    context.startActivity(Intent.createChooser(shareIntent, "Share this image"));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Unable to share. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
