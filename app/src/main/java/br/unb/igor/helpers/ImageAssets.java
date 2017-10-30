package br.unb.igor.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import br.unb.igor.R;

public class ImageAssets {

    public static int builtInBackgrounds[] = {
            R.drawable.miniatura_coast,
            R.drawable.miniatura_corvali,
            R.drawable.miniatura_heartlands,
            R.drawable.miniatura_krevast,
            R.drawable.miniatura_sky
    };

    public static int getBackgroundResource(int imageIndex){
        if (imageIndex < 0 || imageIndex >= builtInBackgrounds.length) {
            imageIndex = 0;
        }
        return builtInBackgrounds[imageIndex];
    }

    public static int getBuiltInBackgroundCount() {
        return builtInBackgrounds.length;
    }

    public static Bitmap getBitmapFromUri(Context ctx, Uri uri, int size) {
        try {
            InputStream input = ctx.getContentResolver().openInputStream(uri);

            if (input == null)
                return null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            input.close();

            if ((options.outWidth == -1) || (options.outHeight == -1)) {
                return null;
            }

            int originalSize = (options.outHeight > options.outWidth)
                    ? options.outHeight
                    : options.outWidth;
            double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
            int sampleSize = Integer.highestOneBit((int) ratio);

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = sampleSize;
            input = ctx.getContentResolver().openInputStream(uri);

            if (input == null)
                return null;

            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();
            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

}
