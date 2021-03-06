/**
 * 
 */
package com.liangshan.jianjian.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.os.Environment;

/**
 * @author jornel
 *
 */
public class ImageUtil {
    
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    
    public static File getBitmapFile(Bitmap bitmap){
        File mPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
        try {  
        mPhotoFile.createNewFile();
        FileOutputStream fOut = null;
        fOut = new FileOutputStream(mPhotoFile);
        //ByteArrayOutputStream out = new ByteArrayOutputStream();   
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);   
         
            fOut.flush();   
            fOut.close();   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
        return mPhotoFile;   
    }
    
    private static String getPhotoFileName() {  
        Date date = new Date(System.currentTimeMillis());  
        SimpleDateFormat dateFormat = new SimpleDateFormat(  
                "'IMG'_yyyyMMddHHmmss_'crop'");  
        return dateFormat.format(date) + ".jpg";  
    }

}
