package jp.techacademy.atsushi.ninomiya.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.ArrayList;
import android.widget.ImageView;
import java.util.Timer;
import java.util.TimerTask;
import android.view.View;
import android.os.Handler;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    int mPosition = 0;
    ArrayList<Uri> imageUris = new ArrayList<Uri>();
    ImageView imageView;

    boolean mIsSlideshow = false;

    public class MainTimerTask extends TimerTask {
        @Override
        public void run () {
            if(mIsSlideshow) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run () {
                        movePosition(1);
                    }
                });
            }
        }

    }

    Timer mTimer = new Timer();
    TimerTask mTimerTask = new MainTimerTask();
    Handler mHandler = new Handler ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("");

        if (imageUris.size() > 0) {
            Uri uri = imageUris.get(mPosition);
            imageView.setImageURI(uri);
        } else {
            textView.setText("画像が保存されていません。");
        }

        mTimer.schedule(mTimerTask, 0, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );


        if (cursor.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                imageUris.add(imageUri);
                Log.d("ANDROID", "URI : " + imageUri.toString());

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void movePosition(int move) {

        mPosition = mPosition + move;

        if (mPosition >= imageUris.size()) {
            mPosition = 0;
        } else if (mPosition < 0) {
            mPosition = imageUris.size() - 1;
        }
        Uri uri = imageUris.get(mPosition);
        imageView.setImageURI(uri);
    }

    public void onbutton1Tapped(View view) {
        movePosition(-1);
    }

    public void onbutton3Tapped(View view) {
        movePosition(1);
    }

    public void onbutton2Tapped(View view) {
        mIsSlideshow = !mIsSlideshow;
    }

}