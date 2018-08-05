package de.nisnagel.iogo.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageUtils {
    static public Bitmap convertToBitmap(String data) {
        if (data == null) {
            return null;
        }
        String pureBase64Encoded = data.substring(data.indexOf(",") + 1);
        byte[] decodedString = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

}
