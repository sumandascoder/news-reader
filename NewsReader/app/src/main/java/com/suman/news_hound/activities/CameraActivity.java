//package com.suman.news_reader.activities;
//
///**
// * Created by sumansucharitdas on 5/1/16.
// */
//
//import android.annotation.TargetApi;
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.content.res.Configuration;
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.CompressFormat;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.hardware.Camera;
//import android.hardware.Camera.CameraInfo;
//import android.hardware.Camera.ErrorCallback;
//import android.hardware.Camera.Parameters;
//import android.hardware.Camera.PictureCallback;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.design.widget.CoordinatorLayout;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.NavigationView;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.ContextThemeWrapper;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.Surface;
//import android.view.SurfaceHolder;
//import android.view.SurfaceHolder.Callback;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.readystatesoftware.systembartint.SystemBarTintManager;
//import com.suman.news_reader.R;
//import com.suman.news_reader.media_controllers.FocusSound;
//import AboutActivity;
//import NROlderNewsList;
//import OlderNewsFileNamesPOJO;
//import NROnboardingActivity;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author sumansucharitdas
// * Unused class
// */
//public class CameraActivity extends AppCompatActivity implements Callback, OnClickListener,Camera.AutoFocusCallback, View.OnTouchListener {
//
//    private SurfaceView surfaceView;
//    private SurfaceHolder surfaceHolder;
//    private Camera camera;
//    private FloatingActionButton flipCameraButton;
//    private FloatingActionButton flashCameraButton;
//    private FloatingActionButton captureImageButton;
//    private Button useCaptureImageButton;
//    private Button retakeImageButton;
//    private FocusSound focusSound;
//    private int focusAreaSize;
//    private Matrix matrix;
//
//    private int cameraId;
//    private boolean flashmode = false;
//    private int rotation;
//    private DrawerLayout            drawerLayout;
//    private ActionBarDrawerToggle   drawerToggle;
//    private NavigationView navView;
//    public static String            FILE_NAME = "temp.jpg";
//    public static String uriOfFile = "";
//    private CoordinatorLayout rootLayout;
//    public static final int         OLDER_NEWS_REQUEST = 5;
//    public static final String      PREF_USER_FIRST_TIME = "user_first_time";
//
//    private boolean                 isUserFirstTime;
//    private static final String     PREFERENCES_FILE = "materialsample_settings";
//    private Rect focusRect;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        isUserFirstTime = Boolean.valueOf(readSharedSetting(CameraActivity.this, PREF_USER_FIRST_TIME, "true"));
//        Intent introIntent = new Intent(CameraActivity.this, NROnboardingActivity.class);
//        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);
//
//        if (isUserFirstTime)
//            startActivity(introIntent);
//        else {
//            setContentView(R.layout.activity_camera);
//            focusSound = new FocusSound();
//            focusAreaSize = getResources().getDimensionPixelSize(R.dimen.camera_focus_area_size);
//            matrix = new Matrix();
//            drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutCamera);
//            drawerToggle = new ActionBarDrawerToggle(CameraActivity.this, drawerLayout, R.string.app_name, R.string.app_name);
//            drawerLayout.setDrawerListener(drawerToggle);
//            navView = (NavigationView) findViewById(R.id.navigationCamera);
//            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCamera);
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//            rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayoutCamera);
//
//            // Coloring and maintaining Material design UI
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                setTranslucentStatus(true);
//            }
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setNavigationBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
//            tintManager.setNavigationBarTintColor(R.color.colorPrimary);
//
//            // camera surface view created
//            cameraId = CameraInfo.CAMERA_FACING_BACK;
//            flipCameraButton = (FloatingActionButton) rootLayout.findViewById(R.id.flipCamera);
//            flashCameraButton = (FloatingActionButton) rootLayout.findViewById(R.id.flash);
//            captureImageButton = (FloatingActionButton) rootLayout.findViewById(R.id.captureImage);
//            useCaptureImageButton = (Button) findViewById(R.id.useCaptureImage);
//            retakeImageButton = (Button) findViewById(R.id.retake);
//            surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
//            surfaceView.setOnTouchListener(this);
//            surfaceView.setZOrderOnTop(false);
//            surfaceHolder = surfaceView.getHolder();
//            surfaceHolder.addCallback(this);
//            flipCameraButton.setOnClickListener(this);
//            captureImageButton.setOnClickListener(this);
//            flashCameraButton.setOnClickListener(this);
//            useCaptureImageButton.setOnClickListener(this);
//            retakeImageButton.setOnClickListener(this);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            new OlderNewsFileNamesPOJO();
//
//            if (Camera.getNumberOfCameras() > 1) {
//                flipCameraButton.setVisibility(View.VISIBLE);
//            }
//            if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
//                flashCameraButton.setVisibility(View.GONE);
//            }
//
//            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(MenuItem menuItem) {
//                    menuItem.setChecked(true);
//                    if (menuItem.getItemId() == R.id.nav_older_news) {
//                        Intent newsActivity = new Intent(getApplication(), NROlderNewsList.class);
//                        releaseCamera();
//                        startActivityForResult(newsActivity, OLDER_NEWS_REQUEST);
//                    }
//                    else if (menuItem.getItemId() == R.id.nav_capture_image) {
//                        // Same screen do nothing
//                    }
//                    else if (menuItem.getItemId() == R.id.nav_load_from_gallery) {
//                        Intent mainActivity = new Intent(CameraActivity.this, NRMainActivity.class);
//                        mainActivity.putExtra("selectedNav","Gallery");
//                        setResult(RESULT_OK);
//                        releaseCamera();
//                        startActivity(mainActivity);
//                        finish();
//                    }
//                    else if (menuItem.getItemId() == R.id.nav_about) {
//                        Intent aboutIntent = new Intent(CameraActivity.this, AboutActivity.class);
//                        startActivity(aboutIntent);
//                    }
//                    else if (menuItem.getItemId() == R.id.nav_contact){
//                        Intent i = new Intent(Intent.ACTION_SEND);
//                        i.setType("message/rfc822");
//                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sumandas.freaky@gmail.com"});
//                        i.putExtra(Intent.EXTRA_SUBJECT, "Say hello or let us know whatsup?");
//                        i.putExtra(Intent.EXTRA_TEXT   , "Please add details");
//                        try {
//                            startActivity(Intent.createChooser(i, "Contact Us"));
//                        } catch (android.content.ActivityNotFoundException ex) {
//                            Toast.makeText(CameraActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    drawerLayout.closeDrawers();
//                    return true;
//                }
//            });
//        }
//    }
//
//    // Set translucent status for api below 19, material design
//    @TargetApi(19) private void setTranslucentStatus(boolean on) {
//        Window win = getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    protected void onPause () {
//        super.onPause();
//        releaseCamera();
//    }
//
//    @Override
//    protected void onStop (){
//        super.onStop();
//        releaseCamera();
//    }
//
//    @Override
//    protected void onResume () {
//        super.onResume();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
//        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
//        return sharedPref.getString(settingName, defaultValue);
//    }
//
//    @Override
//    public void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        if(drawerToggle != null){
//            drawerToggle.syncState();
//        }
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        drawerToggle.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (drawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        if (!openCamera(CameraInfo.CAMERA_FACING_BACK)) {
//            alertCameraDialog();
//        }
//    }
//
//    private boolean openCamera(int id) {
//        boolean result = false;
//        cameraId = id;
//        releaseCamera();
//        try {
//            camera = Camera.open(cameraId);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (camera != null) {
//            try {
//                setUpCamera(camera);
//                camera.setErrorCallback(new ErrorCallback() {
//
//                    @Override
//                    public void onError(int error, Camera camera) {}
//                });
//                camera.setPreviewDisplay(surfaceHolder);
//                camera.startPreview();
//                result = true;
//            } catch (IOException e) {
//                e.printStackTrace();
//                result = false;
//                releaseCamera();
//            }
//        }
//        return result;
//    }
//
//    private void setUpCamera(Camera c) {
//        Camera.CameraInfo info = new Camera.CameraInfo();
//        Camera.getCameraInfo(cameraId, info);
//        rotation = getWindowManager().getDefaultDisplay().getRotation();
//
//        int degree = 0;
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                degree = 0;
//                break;
//            case Surface.ROTATION_90:
//                degree = 90;
//                break;
//            case Surface.ROTATION_180:
//                degree = 180;
//                break;
//            case Surface.ROTATION_270:
//                degree = 270;
//                break;
//
//            default:
//                break;
//        }
//
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            // frontFacing
//            rotation = (info.orientation + degree) % 330;
//            rotation = (360 - rotation) % 360;
//        } else {
//            // Back-facing
//            rotation = (info.orientation - degree + 360) % 360;
//        }
//        c.setDisplayOrientation(rotation);
//        Parameters params = c.getParameters();
//
//        showFlashButton(params);
//
//        List<String> focusModes = params.getSupportedFlashModes();
//        if (focusModes != null) {
//            if (focusModes
//                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
//                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            }
//        }
//
//        params.setRotation(rotation);
//
//        if (params.getMaxNumMeteringAreas() > 0){ // check that metering areas are supported
//            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
//
//            Rect areaRect1 = new Rect(-100, -100, 100, 100);    // specify an area in center of image
//            meteringAreas.add(new Camera.Area(areaRect1, 600)); // set weight to 60%
//            Rect areaRect2 = new Rect(800, -1000, 1000, -800);  // specify an area in upper right of image
//            meteringAreas.add(new Camera.Area(areaRect2, 400)); // set weight to 40%
//            params.setMeteringAreas(meteringAreas);
//
//        }
//        camera.setParameters(params);
//    }
//
//    private void showFlashButton(Parameters params) {
//        boolean showFlash = (getPackageManager().hasSystemFeature(
//                PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)
//                && params.getSupportedFlashModes() != null
//                && params.getSupportedFocusModes().size() > 1;
//
//        flashCameraButton.setVisibility(showFlash ? View.VISIBLE
//                : View.INVISIBLE);
//
//    }
//
//    private void releaseCamera() {
//        try {
//            if (camera != null) {
//                camera.setPreviewCallback(null);
//                camera.setErrorCallback(null);
//                camera.stopPreview();
//                camera.release();
//                camera = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("error", e.toString());
//            camera = null;
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.flash:
//                flashOnButton();
//                break;
//            case R.id.flipCamera:
//                flipCamera();
//                break;
//            case R.id.captureImage:
//                takeImage();
//                break;
//            case R.id.useCaptureImage:
//                Intent mainActivity = new Intent(CameraActivity.this, NRMainActivity.class);
//                mainActivity.putExtra("selectedNav","CaptureImage");
//                mainActivity.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(uriOfFile)));
//                setResult(RESULT_OK);
//                startActivity(mainActivity);
//                releaseCamera();
//                finish();
//                break;
//            case R.id.retake:
//                openCamera(CameraInfo.CAMERA_FACING_BACK);
//                captureImageButton.setVisibility(View.VISIBLE);
//                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
//                    flashCameraButton.setVisibility(View.VISIBLE);
//                }
//                flipCameraButton.setVisibility(View.VISIBLE);
//                useCaptureImageButton.setVisibility(View.INVISIBLE);
//                retakeImageButton.setVisibility(View.INVISIBLE);
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void takeImage() {
//        camera.takePicture(null, null, new PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//                try {
//                    // convert byte array into bitmap
//                    Bitmap loadedImage = null;
//                    Bitmap rotatedBitmap = null;
//                    loadedImage = BitmapFactory.decodeByteArray(data, 0,
//                            data.length);
//
//                    // rotate Image
//                    Matrix rotateMatrix = new Matrix();
//                    rotateMatrix.postRotate(rotation);
//                    rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
//                            loadedImage.getWidth(), loadedImage.getHeight(),
//                            rotateMatrix, false);
//
//                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                    File f = new File(dir, FILE_NAME);
//
//                    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
//
//                    // save image into gallery
//                    rotatedBitmap.compress(CompressFormat.JPEG, 100, ostream);
//
//                    FileOutputStream fout = new FileOutputStream(f);
//                    fout.write(ostream.toByteArray());
//                    fout.close();
//                    uriOfFile = f.getAbsolutePath();
//
//                    captureImageButton.setVisibility(View.INVISIBLE);
//                    flashCameraButton.setVisibility(View.INVISIBLE);
//                    flipCameraButton.setVisibility(View.INVISIBLE);
//                    useCaptureImageButton.setVisibility(View.VISIBLE);
//                    retakeImageButton.setVisibility(View.VISIBLE);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }
//
//    private void flipCamera() {
//        int id = (cameraId == CameraInfo.CAMERA_FACING_BACK ? CameraInfo.CAMERA_FACING_FRONT
//                : CameraInfo.CAMERA_FACING_BACK);
//        if (!openCamera(id)) {
//            alertCameraDialog();
//        }
//    }
//
//    private void alertCameraDialog() {
//        AlertDialog.Builder dialog = createAlert(CameraActivity.this,
//                "Camera info", "error to open camera");
//        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//
//            }
//        });
//
//        dialog.show();
//    }
//
//    private Builder createAlert(Context context, String title, String message) {
//
//        AlertDialog.Builder dialog = new AlertDialog.Builder(
//                new ContextThemeWrapper(context,
//                        android.R.style.Theme_Holo_Light_Dialog));
//        dialog.setIcon(R.mipmap.ic_launcher);
//        if (title != null)
//            dialog.setTitle(title);
//        else
//            dialog.setTitle("Information");
//        dialog.setMessage(message);
//        dialog.setCancelable(false);
//        return dialog;
//
//    }
//
//    private void flashOnButton() {
//        if (camera != null) {
//            try {
//                Parameters param = camera.getParameters();
//                param.setFlashMode(!flashmode ? Parameters.FLASH_MODE_TORCH
//                        : Parameters.FLASH_MODE_OFF);
//                camera.setParameters(param);
//                flashmode = !flashmode;
//            } catch (Exception e) {}
//
//        }
//    }
//
//    @Override
//    public void onAutoFocus(boolean success, Camera camera) {
//        //play default system sound if exists
//        if (success) {
//            focusSound.play();
//        }
//    }
//
//    /**
//     * Convert touch position x:y to {@link Camera.Area} position -1000:-1000 to 1000:1000.
//     * <p>
//     * Rotate, scale and translate touch rectangle using matrix configured in
//     * {@link SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)}
//     */
//    private Rect calculateTapArea(float x, float y, float coefficient) {
//        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
//
//        int left = clamp((int) x - areaSize / 2, 0, surfaceView.getWidth() - areaSize);
//        int top = clamp((int) y - areaSize / 2, 0, surfaceView.getHeight() - areaSize);
//
//        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
//        matrix.mapRect(rectF);
//
//        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
//    }
//
//    private int clamp(int x, int min, int max) {
//        if (x > max) {
//            return max;
//        }
//        if (x < min) {
//            return min;
//        }
//        return x;
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
//        List<Camera.Area> mylist = new ArrayList<Camera.Area>();
//        mylist.add(new Camera.Area(focusRect, 1000));
//        Parameters parameters = camera.getParameters();
//        parameters.setFocusAreas(mylist);
//        camera.setParameters(parameters);
//        return false;
//    }
//}