package com.suman.news_hound.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.suman.news_hound.R;
import com.suman.news_hound.media_controllers.NRAudioControllerView;
import com.suman.news_hound.media_controllers.NRMusicPlayerFragment;
import com.suman.news_hound.navigation_informational.AboutActivity;
import com.suman.news_hound.navigation_older_news.NROlderNewsList;
import com.suman.news_hound.navigation_older_news.OlderNewsFileNamesPOJO;
import com.suman.news_hound.utils.CameraUtils;
import com.suman.news_hound.utils.ImageUtils;
import com.suman.news_hound.utils.PermissionUtils;
import com.suman.news_hound.utils.ViewUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * @author ssucharitdas
 * Ref: From Google Cloud Vision Sameple APIs
 * Class does the following:
 * - Allows user to interact with Cloud Vision API (OCR)
 * - Let user upload images and extracts the text out of it
 * - Once processed let user listen to the text read out to him.
 */
public class NRMainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, LanguageDialog.AlertPositiveListener {

    public static final String      FILE_NAME = "temp.jpg";
    public static final int         CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int         CAMERA_IMAGE_REQUEST = 3;
    public static final int         MUSIC_PLAYER_REQUEST = 4;
    public static final int         OLDER_NEWS_REQUEST = 5;
    public static final int         CAMERA_CROP_IMAGE_REQUEST = 5;

    private static final String     TAG = NRMainActivity.class.getSimpleName();
    private static final int        GALLERY_IMAGE_REQUEST = 1;
    private static final String     CLOUD_VISION_API_KEY = "AIzaSyDsKNjWffflDGbZhyK8q8U-ZTlqxC1OlyI";//"AIzaSyCsGPZ_UVj0GRT6ii4GojaiDYf2c06lDDQ";
    public static String                  speechText = "I have nothing to speak of now.";
    public static String                  fileID;
    private HashMap<String, String> map = new HashMap<String, String>();
    private String                  dir = Environment.getExternalStorageDirectory() + "/NewsReader/";
    private File                    f = new File(dir);
    private int                     position;
    public static String                  currentLanguageCode = "en";
    private static Uri              imagePath;

    // UI elements
    public static TextView                progressStatusText;
    public static TextView                imageStatusText;
    public static TextView                imageDetailsText;
    public static Bitmap bitmap;
    public CloudVision cloudVision;

    private ImageView               mMainImage;
    public static ImageView               blurImage;
    private TextToSpeech            tts;
    public static FloatingActionButton readFABButton;
    public static ProgressBar             progressBarImageExtract;
    private DrawerLayout            drawerLayout;
    private ActionBarDrawerToggle   drawerToggle;
    private NavigationView          navView;
    private CoordinatorLayout       rootLayout;

    // Personal classes
    private GoogleTranslate         translator;

    private CameraUtils             cameraUtils;
    private ViewUtils               viewUtils;
    private NRMusicPlayerFragment musicPlayerFragment;
    private FragmentTransaction fragmentTransaction;
    public static NRAudioControllerView controller;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(NRMainActivity.this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        new OlderNewsFileNamesPOJO();

        cameraUtils = new CameraUtils();
        viewUtils = new ViewUtils();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        musicPlayerFragment = (NRMusicPlayerFragment)
                getSupportFragmentManager().findFragmentById(R.id.music_player_fragment);
        fragmentTransaction.hide(musicPlayerFragment);
        fragmentTransaction.commit();

        navView = (NavigationView) findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                if (menuItem.getItemId() == R.id.nav_older_news) {
                    Intent newsActivity = new Intent(getApplication(), NROlderNewsList.class);
                    startActivityForResult(newsActivity, OLDER_NEWS_REQUEST);
                }
                /* else if(menuItem.getItemId() == R.id.nav_lang) {
                        FragmentManager manager = getFragmentManager();

                        // Instantiating the DialogFragment class
                        LanguageDialog alert = new LanguageDialog();

                        // Creating a bundle object to store the selected item's index
                        Bundle b  = new Bundle();

                        // Storing the selected item's index in the bundle object
                        b.putInt("position", position);

                        // Setting the bundle object to the dialog fragment object
                        alert.setArguments(b);

                        // Creating the dialog fragment object, which will in turn open the alert dialog window
                        alert.show(manager, "alert_dialog_radio");
                    } **/
                else if (menuItem.getItemId() == R.id.nav_load_from_gallery) {
                    startGalleryChooser();
                } else if (menuItem.getItemId() == R.id.nav_capture_image) {
                    startCamera();
                } else if (menuItem.getItemId() == R.id.nav_about) {
                    Intent aboutIntent = new Intent(NRMainActivity.this, AboutActivity.class);
                    startActivity(aboutIntent);
                } else if (menuItem.getItemId() == R.id.nav_contact) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"sumandas.freaky@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Say hello or let us know whatsup?");
                    i.putExtra(Intent.EXTRA_TEXT, "Please add details");
                    try {
                        startActivity(Intent.createChooser(i, "Contact Us"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(NRMainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        controller = new NRAudioControllerView(getApplicationContext());

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        // Coloring and maintaining Material design UI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            viewUtils.setTranslucentStatus(true, NRMainActivity.this);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
        tintManager.setNavigationBarTintColor(R.color.colorPrimary);

        // UI id assignment
        readFABButton = (FloatingActionButton) findViewById(R.id.read_FABbutton);
        progressBarImageExtract = (ProgressBar) findViewById(R.id.progressBar);
        mMainImage = (ImageView) findViewById(R.id.main_image);
        blurImage = (ImageView) findViewById(R.id.blur_ocr_image);
        imageStatusText = (TextView) findViewById(R.id.image_status);
        progressStatusText = (TextView) findViewById(R.id.process_status);
        imageDetailsText = (TextView) findViewById(R.id.image_details);

        progressStatusText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf"));
        imageStatusText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf"));
        imageDetailsText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf"));

        if (fileID == null) {
            fileID = UUID.randomUUID().toString();
        }
        tts = new TextToSpeech(this, this);
        tts.setLanguage(Locale.US);
        readFABButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFABButton.setVisibility(View.INVISIBLE);
                controller.setMediaPlayer(musicPlayerFragment);
                controller.setAnchorView((FrameLayout) findViewById(R.id.frame_main_activity));
                musicPlayerFragment.setStream();
                controller.show();
            }
        });

        if (getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT) != null) {
            performCrop((Uri) getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT));
        }
        else if (getIntent().getStringExtra("selectedNav") != null) {
            if(getIntent().getStringExtra("selectedNav").equals("Gallery")){
                startGalleryChooser();
            }
        }

        if(getIntent().getBooleanExtra("olderNews", false) == true){
            fileID = getIntent().getStringExtra("fileID");
            readFABButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagePath = Uri.parse(data.getDataString());
            uploadImage(data.getData());
        }
        else if(requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            performCrop(Uri.fromFile(getCameraFile()));
        }
        else if(requestCode == CAMERA_CROP_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imagePath = Uri.parse(data.getDataString());
            uploadImage(imagePath);
        }
        else if(requestCode == MUSIC_PLAYER_REQUEST && resultCode == RESULT_OK){
            // DO NOTHING
        }
        else if(requestCode == OLDER_NEWS_REQUEST && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            if (extras != null){
                String selectedNav  = extras.getString("selectedNav");
                if(selectedNav.equals("CaptureImage")){
                    startCamera();
                }
                else if (selectedNav.equals("Gallery")){
                    startGalleryChooser();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
            startCamera();
        }
        else {
            cameraUtils.setCameraPermissionsRequest(NRMainActivity.this);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(drawerToggle != null){
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(drawerToggle != null){
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume () {
        super.onResume();
    }

    @Override
    protected void onStop (){
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS && ! speechText.equals("I have nothing to speak of now.")) {
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, fileID);
            if (!f.exists()) {
                f.mkdirs();
            }
            if(new File(f + "/" + fileID + ".wav").exists()){
                fileID = UUID.randomUUID().toString();
                new File(f + "/" + fileID + ".wav");
            }
            tts.setLanguage(new Locale(currentLanguageCode));
            if(tts.synthesizeToFile(speechText, map, f + "/" + fileID + ".wav") == TextToSpeech.SUCCESS);
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            readFABButton.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onError(String utteranceId) {
                }
            });
        } else {
            Log.e("TTS", "TTS Initialization Failed!");
        }
    }

    @Override
    public void onPositiveClick(int position) {
        this.position = position;
        new EnglishToTagalog().execute();
    }

    private void performCrop(Uri picUri){
        //call the standard crop action intent (the user device may not support it)
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri
        cropIntent.setDataAndType(picUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop : 1:1 means square
        //cropIntent.putExtra("aspectX", 1);
        //cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
        startActivityForResult(cropIntent, CAMERA_CROP_IMAGE_REQUEST);
    }

    // Choose image from photo gallery
    public void startGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST);
    }

    // Start camera to capture image : Also called from NROlderList
    public void startCamera() {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
//            Intent intent = new Intent(this,CameraActivity.class);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(CameraActivity.uriOfFile)));
//            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    // Get the file for Media
    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // Scale the image to 800px to save on bandwidth
                int orientation = ImageUtils.setOrientation(uri);
                bitmap = ImageUtils.scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 600, orientation);
                //bitmap = ImageUtils.convertToEdgeDetector(bitmap);
                //int x = bitmap.getByteCount();
                mMainImage.setBackgroundResource(R.drawable.image_background);
                mMainImage.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bitmap, 20));
                bitmap = ImageUtils.convertColorIntoBlackAndWhiteImage(bitmap);
                callCloudVision(bitmap);
            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    // Cloud API making the call for OCR
    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        imageStatusText.setText(R.string.loading_message);

        cloudVision = new CloudVision(NRMainActivity.this, CLOUD_VISION_API_KEY);
        cloudVision.execute(bitmap);
    }

    // Extract the string from the Google OCR service
    public void initSpeech() {
        onInit(TextToSpeech.SUCCESS);
    }

    private class EnglishToTagalog extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                translator = new GoogleTranslate("AIzaSyDPE9XuoTIMuaDcrRMY4GzSUNBpqmkjuZs");
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            // Start the progress dialog
            progress = ProgressDialog.show(NRMainActivity.this, null, "Translating...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();
            super.onPostExecute(result);
            translated();
            currentLanguageCode = Language.code[position];
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    // Translation of text to different language
    public void translated() {
        String translatetotagalog = imageDetailsText.getText().toString();
        String text = translator.translate(translatetotagalog, currentLanguageCode, Language.code[position]);
        imageDetailsText = (TextView) findViewById(R.id.image_details);
        imageDetailsText.setText(text);
        tts.setLanguage(new Locale(Language.code[position]));
        speechText = text;
        onInit(TextToSpeech.SUCCESS);
    }
}