package com.suman.news_hound.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.graphics.Matrix;

import java.io.IOException;

/**
 * @author sumansucharitdas
 */
public class ImageUtils {

    final static int KERNAL_WIDTH = 3;
    final static int KERNAL_HEIGHT = 3;

    static int[][] kernel ={
            {0, -1, 0},
            {-1, 4, -1},
            {0, -1, 0}
    };

    Bitmap bitmap_Source;

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xFF00695C;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     *
     * @param bitmap The source bitmap
     * @param maxDimension Pixel dimension to be used
     * @param orientation Set the orientation
     * @return
     */
    public static Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension, int orientation) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }

        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        }
        else if (orientation == 3) {
            matrix.postRotate(180);
        }
        else if (orientation == 8) {
            matrix.postRotate(270);
        }
        // Create the scaled bitmap image
        Bitmap b = Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
    }

    /**
     * Set the orientation for the image file
     * @param uri File path
     * @return
     */
    public static int setOrientation(Uri uri){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(uri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
    }

    /**
     * Convert the image to Gray scale
     * @param bmpOriginal
     * @return
     */
    public static Bitmap convertColorIntoBlackAndWhiteImage(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix(
                new float[]{
                        -1, 0, 0, 0, 255,
                        0, -1, 0, 0, 255,
                        0, 0, -1, 0, 255,
                        0, 0, 0, 1, 0
                });
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * Convert image to edge detected image
     * @param src The source bitmap
     * @return
     */
    public static Bitmap convertToEdgeDetector(Bitmap src){
        Bitmap dest = Bitmap.createBitmap(
                src.getWidth(), src.getHeight(), src.getConfig());

        int bmWidth = src.getWidth();
        int bmHeight = src.getHeight();
        int bmWidth_MINUS_2 = bmWidth - 2;
        int bmHeight_MINUS_2 = bmHeight - 2;

        for(int i = 1; i <= bmWidth_MINUS_2; i++){
            for(int j = 1; j <= bmHeight_MINUS_2; j++){

                //get the surround 3*3 pixel of current src[i][j] into a matrix subSrc[][]
                int[][] subSrc = new int[KERNAL_WIDTH][KERNAL_HEIGHT];
                for(int k = 0; k < KERNAL_WIDTH; k++){
                    for(int l = 0; l < KERNAL_HEIGHT; l++){
                        subSrc[k][l] = src.getPixel(i-1+k, j-1+l);
                    }
                }

                //subSum = subSrc[][] * knl[][]
                int subSumA = 0;
                int subSumR = 0;
                int subSumG = 0;
                int subSumB = 0;

                for(int k = 0; k < KERNAL_WIDTH; k++){
                    for(int l = 0; l < KERNAL_HEIGHT; l++){
                        subSumR += Color.red(subSrc[k][l]) * kernel[k][l];
                        subSumG += Color.green(subSrc[k][l]) * kernel[k][l];
                        subSumB += Color.blue(subSrc[k][l]) * kernel[k][l];
                    }
                }

                subSumA = Color.alpha(src.getPixel(i, j));

                if(subSumR <0){
                    subSumR = 0;
                }else if(subSumR > 255){
                    subSumR = 255;
                }

                if(subSumG <0){
                    subSumG = 0;
                }else if(subSumG > 255){
                    subSumG = 255;
                }

                if(subSumB <0){
                    subSumB = 0;
                }else if(subSumB > 255){
                    subSumB = 255;
                }

                dest.setPixel(i, j, Color.argb(
                        subSumA,
                        subSumR,
                        subSumG,
                        subSumB));
            }
        }

        return dest;
    }
}