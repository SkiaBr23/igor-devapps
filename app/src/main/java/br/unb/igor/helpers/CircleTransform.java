package br.unb.igor.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class CircleTransform implements Transformation {

    private static Canvas canvas = new Canvas();
    private static Paint paint = new Paint();

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        float r = size / 2f;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        if (squaredBitmap != source) {
            source.recycle();
        }

        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);

        paint.setShader(shader);
        paint.setAntiAlias(true);
        canvas.setBitmap(bitmap);
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}
