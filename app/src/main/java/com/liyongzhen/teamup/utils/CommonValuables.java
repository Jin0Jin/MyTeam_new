package com.liyongzhen.teamup.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by alex2 on 10/13/2017.
 */

public class CommonValuables {
    public static final String TAG = "---";
    public static final DatabaseReference referenceFollowings = FirebaseDatabase.getInstance().getReference().child("followings");

    public static Bitmap circleResizedBitmap(Bitmap bitmap, int radius){
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap result = Bitmap.createBitmap(radius*2,radius*2, conf);
        Canvas canvas = new Canvas(result);
        final int color2 = 0xff424242;
        final Rect rect = new Rect(0, 0, radius*2, radius*2);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color2);
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, new Rect(0,0, bitmap.getWidth(),bitmap.getHeight()), rect, paint);
        return result;
    }
    public static int[] convertDateStringToInt(String str){
        int[] returnValue = {1988,10,26};
        String[] splitedString = str.split("/");
        try {
            for (int i = 0; i < 3 && i < splitedString.length; i++) {
                returnValue[i] = Integer.parseInt(splitedString[i]);
            }
        } catch (Exception e){
            Log.d("---", e.toString());
        }

        return returnValue;
    }

    public static boolean checkFirebaseAuth(){
        return null != FirebaseAuth.getInstance().getCurrentUser();
    }
}
