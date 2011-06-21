/**
 * 
 */
package com.liangshan.jianjian.android;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author ezhuche
 *
 */
public class TakePhotoActivity extends Activity {
    public static final String TAG = "TakePhotoActivity";
    public static final boolean DEBUG = JianjianSettings.DEBUG;
    private Button mCameraButton;
    private Button mGallaryButton;
    private ImageView mPhotoImage;
    private Button mConfirmButton;
    private Button mCancelButton;
    private LinearLayout mButtonAreaLayout;
    private TextView mPhotoframeText;
    
    private Bitmap mPhoto;
    
    private static final int CAMERA_WITH_DATA = 1001;  
    
    /*������ʶ����gallery��activity*/  
    private static final int PHOTO_PICKED_WITH_DATA = 1002;  
  
    /*���յ���Ƭ�洢λ��*/  
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    protected static final String EXTRA_PHOTO_RETURNED = "UPLOADED_PHOTO";  
  
    private File mCurrentPhotoFile;//��������յõ���ͼƬ  
    
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.d(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.take_photo_activity);
        
        registerReceiver(mLoggedOutReceiver, new IntentFilter(Jianjianroid.INTENT_ACTION_LOGGED_OUT));

        mCameraButton = (Button) findViewById(R.id.camerabutton);
        mGallaryButton = (Button) findViewById(R.id.gallarybutton);
        mConfirmButton = (Button) findViewById(R.id.confirmbutton);
        mCancelButton = (Button) findViewById(R.id.cancelbutton);
        mPhotoImage = (ImageView) findViewById(R.id.photoImage);
        mButtonAreaLayout = (LinearLayout) findViewById(R.id.ButtonArea);
        mPhotoframeText = (TextView) findViewById(R.id.photoframeText);
        
        
        
        
        mPhotoImage.setVisibility(View.GONE);
        mPhotoImage.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mPhotoImage.setVisibility(View.GONE);
                mCameraButton.setVisibility(View.VISIBLE);
                mGallaryButton.setVisibility(View.VISIBLE);
                mCameraButton.setEnabled(true);
                mGallaryButton.setEnabled(true);
                mConfirmButton.setEnabled(false);
                mPhotoframeText.setText(R.string.upload_photo_label);
            }
            
        });
        mCameraButton.setEnabled(true);
        mCameraButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String status=Environment.getExternalStorageState();  
                if(status.equals(Environment.MEDIA_MOUNTED)){//�ж��Ƿ���SD��  
                    doTakePhoto();// �û�����˴��������ȡ  
                }  
                else{  
                    showToast(getResources().getString(R.string.no_sd_text)); 
                }  
            }
            
        });
        
        mGallaryButton.setEnabled(true);
        mGallaryButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                doPickPhotoFromGallery();
            }
            
        });
        
        mCancelButton.setEnabled(true);
        mCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                finish();
            }
            
        });
        
        mConfirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mPhoto!=null){
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_PHOTO_RETURNED, mPhoto);
                    TakePhotoActivity.this.setResult(Activity.RESULT_OK, intent);
                    TakePhotoActivity.this.finish();
                }else{
                    showToast(getResources().getString(R.string.no_photo_loaded));
                }
                
            }
            
        });
        
        if(mPhoto == null){
            mConfirmButton.setEnabled(false);
            mPhotoframeText.setText(R.string.upload_photo_label);
        }

    }
    
    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);

    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode != RESULT_OK)  {
            return;  
        }       
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {  
            case PHOTO_PICKED_WITH_DATA: {// ����Gallery���ص�  
                final Bitmap photo = data.getParcelableExtra("data");  
                 
                //�����û�ѡ���ͼƬ  
                //img = getBitmapByte(photo);  
                //mEditor.setPhotoBitmap(photo);  
                //System.out.println("set new photo");
                mPhotoImage.setVisibility(View.VISIBLE);
                mCameraButton.setVisibility(View.GONE);
                mGallaryButton.setVisibility(View.GONE);
                mPhotoImage.setScaleType(ImageView.ScaleType.FIT_XY);
                mPhotoImage.setImageBitmap(photo);

                if(photo != null){
                    mPhoto = photo;
                    mConfirmButton.setEnabled(true);
                    mPhotoframeText.setText(R.string.restart_upload_label);
                }
                break;  
            }  
            case CAMERA_WITH_DATA: {// ��������򷵻ص�,�ٴε���ͼƬ��������ȥ�޼�ͼƬ
                //showToast("CAMERA_WITH_DATA=="+mCurrentPhotoFile.toString());
                doCropPhoto(mCurrentPhotoFile);  
                break;  
            }  
        }  
    }  

    /**
     * @param string
     */
    protected void showToast(String string) {
        // TODO Auto-generated method stub
        Toast toast = Toast.makeText(getApplicationContext(),
                string, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 
     */
    protected void doPickPhotoFromGallery() {
        // TODO Auto-generated method stub
        try {  
            // Launch picker to choose photo for selected contact  
            final Intent intent = getPhotoPickIntent();  
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);  
        } catch (ActivityNotFoundException e) {  
            Toast.makeText(this, R.string.photoPicker_NotFound_text,  
                    Toast.LENGTH_LONG).show();  
        }  
    }

    /**
     * 
     */
    protected void doTakePhoto() {
        // TODO Auto-generated method stub
        try {  
            // Launch camera to take photo for selected contact  
            PHOTO_DIR.mkdirs();// ������Ƭ�Ĵ洢Ŀ¼  
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// �����յ���Ƭ�ļ�����

            final Intent intent = getTakePhotoIntent(mCurrentPhotoFile);  
            startActivityForResult(intent, CAMERA_WITH_DATA);  
        } catch (ActivityNotFoundException e) {  
            Toast.makeText(this, R.string.photoPicker_NotFound_text,  
                    Toast.LENGTH_LONG).show();  
        } 
    }
    
    /**
     * @param f
     * @return
     */
    private Intent getTakePhotoIntent(File f) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);  
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));  
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        return intent;  
    }
    
    // ��װ����Gallery��intent  
    public static Intent getPhotoPickIntent() {  
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);  
        intent.setType("image/*");  
        intent.putExtra("crop", "true");  
        intent.putExtra("aspectX", 1);  
        intent.putExtra("aspectY", 1);  
        intent.putExtra("outputX", 200);  
        intent.putExtra("outputY", 200);  
        intent.putExtra("return-data", true);  
        return intent;  
    }  

    /** 
     * �õ�ǰʱ���ȡ�õ�ͼƬ���� 
     *  
     */  
    private String getPhotoFileName() {  
        Date date = new Date(System.currentTimeMillis());  
        SimpleDateFormat dateFormat = new SimpleDateFormat(  
                "'IMG'_yyyyMMddHHmmss");  
        return dateFormat.format(date) + ".jpg";  
    }  
    

    protected void doCropPhoto(File f) {  
        try {  
            // ����galleryȥ���������Ƭ  
            final Intent intent = getCropImageIntent(Uri.fromFile(f));  
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);  
        } catch (Exception e) {  
            Toast.makeText(this, R.string.photoPicker_NotFound_text,  
                    Toast.LENGTH_LONG).show();  
        }  
    }  
      
    /**  
    * Constructs an intent for image cropping. ����ͼƬ��������  
    */  
    public static Intent getCropImageIntent(Uri photoUri) {  
        Intent intent = new Intent("com.android.camera.action.CROP");  
        intent.setDataAndType(photoUri, "image/*");  
        intent.putExtra("crop", "true");  
        intent.putExtra("aspectX", 1);  
        intent.putExtra("aspectY", 1);  
        intent.putExtra("outputX", 200);  
        intent.putExtra("outputY", 200);  
        intent.putExtra("return-data", true);  
        return intent;  
    } 
    
}