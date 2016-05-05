package com.suman.news_reader.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import android.support.v7.app.ActionBarDrawerToggle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.suman.news_reader.media_controllers.NRMusicPlayerActivity;
import com.suman.news_reader.navigation_informational.AboutActivity;
import com.suman.news_reader.navigation_older_news.NROlderNewsList;
import com.suman.news_reader.navigation_older_news.OlderNewsFileNamesPOJO;
import com.suman.news_reader.utils.ImageUtils;
import com.suman.news_reader.utils.PermissionUtils;
import com.suman.news_reader.R;

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

    private static final String     TAG = NRMainActivity.class.getSimpleName();
    private static final int        GALLERY_IMAGE_REQUEST = 1;
    private static final String     CLOUD_VISION_API_KEY = "AIzaSyCsGPZ_UVj0GRT6ii4GojaiDYf2c06lDDQ";
    private String                  speechText = "I have nothing to speak of now.";
    private String                  fileID;
    private HashMap<String, String> map = new HashMap<String, String>();
    private String                  dir = Environment.getExternalStorageDirectory() + "/NewsReader/";
    private File                    f = new File(dir);

    private TextView                mImageDetails;
    private TextView                imageStatusText;
    private ImageView               mMainImage;
    private TextToSpeech            tts;
    private Button                  readTextButton;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navView;
    CoordinatorLayout rootLayout;
    GoogleTranslate translator;
    int position;
    String currentLanguageCode = "en";
    String[] colors = {"#96CC7A", "#EA705D", "#66BBCC"};
    ProgressBar progressBarImageExtract;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(NRMainActivity.this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        new OlderNewsFileNamesPOJO();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        navView = (NavigationView) findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                if (menuItem.getItemId() == R.id.nav_older_news) {
                    Intent newsActivity = new Intent(getApplication(), NROlderNewsList.class);
                    startActivityForResult(newsActivity,OLDER_NEWS_REQUEST);
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
                }
                else if (menuItem.getItemId() == R.id.nav_capture_image) {
                    startCamera();
                }
                else if (menuItem.getItemId() == R.id.nav_about) {
                    Intent aboutIntent = new Intent(NRMainActivity.this, AboutActivity.class);
                    aboutIntent.putExtra("about-page", "AboutNewsReader.html");
                    startActivity(aboutIntent);
                }
                else if (menuItem.getItemId() == R.id.nav_contact){
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sumandas.freaky@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Say hello or let us know whatsup?");
                    i.putExtra(Intent.EXTRA_TEXT   , "Please add details");
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

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
        tintManager.setNavigationBarTintColor(R.color.colorPrimary);

        fileID = UUID.randomUUID().toString();
        tts = new TextToSpeech(this, this);
        tts.setLanguage(Locale.US);

        readTextButton = (Button) findViewById(R.id.read_button);
        progressBarImageExtract = (ProgressBar) findViewById(R.id.progressBar);
        mImageDetails = (TextView) findViewById(R.id.image_details);
        mMainImage = (ImageView) findViewById(R.id.main_image);
        imageStatusText = (TextView) findViewById(R.id.image_status);

        readTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startMusic = new Intent(getApplication(), NRMusicPlayerActivity.class);
                startMusic.putExtra("speechText", speechText);
                startMusic.putExtra("fileID", fileID);
                startActivityForResult(startMusic, MUSIC_PLAYER_REQUEST);
            }
        });
        readTextButton.setText("Read out loud to me");



        if(getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT) != null){
            uploadImage((Uri) getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT));
        }
    }

    @TargetApi(19) private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void startGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST);
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        }
        else if(requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            uploadImage(Uri.fromFile(getCameraFile()));
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
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // Scale the image to 800px to save on bandwidth
                Bitmap bitmap = ImageUtils.scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 1200);
                mMainImage.setBackgroundResource(R.drawable.image_background);
                mMainImage.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bitmap, 20));
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

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        imageStatusText.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Integer, String>() {
            int max = 0;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBarImageExtract.setVisibility(View.VISIBLE);
                progressBarImageExtract.setProgress(0);
            }

            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();
                        progressBarImageExtract.setMax(imageBytes.length);
                        max = imageBytes.length;

                        // Keep it going till 9/10th so that value on progress bar proceeds upto a point
                        for (int i = 0 ; i < imageBytes.length * 9/10; i++) {
                            publishProgress(i);
                        }
                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // Add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature textDetection = new Feature();
                            textDetection.setType("TEXT_DETECTION");
                            textDetection.setMaxResults(10);
                            add(textDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if(values[0] < max){
                    progressBarImageExtract.setProgress(values[0]);
                }
            }

            protected void onPostExecute(String result) {
                progressBarImageExtract.setProgress(max);
                imageStatusText.setText("Extracted text from Image:");
                mImageDetails.setText(result);
                progressBarImageExtract.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    // Extract the string from the Google OCR service
    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";
        List<EntityAnnotation> textAnnotations = response.getResponses().get(0).getTextAnnotations();
        Log.i("Response", response.toString() + "");
        if (textAnnotations != null) {
            speechText = "";
            speechText += String.format("%s", textAnnotations.get(0).getDescription());
            message += String.format("%s", textAnnotations.get(0).getDescription());
            onInitForImage(TextToSpeech.SUCCESS);
        } else {
            speechText = "I found no text";
            message += "nothing";
        }
        return message;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, fileID);
            if (!f.exists()) {
                f.mkdirs();
            }
            tts.setLanguage(new Locale(Language.code[position]));
            if(tts.synthesizeToFile(speechText, map, f + "/" + fileID + ".wav") == TextToSpeech.SUCCESS);
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {}

                @Override
                public void onDone(String utteranceId) {}

                @Override
                public void onError(String utteranceId) {}
            });
        } else {
            Log.e("TTS", "TTS Initialization Failed!");
        }
    }

    // Method so that read is enabled only when file is synthesized, can't rely on onInit
    private void onInitForImage(int status){
        if (status == TextToSpeech.SUCCESS) {
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, fileID);
            if (!f.exists()) {
                f.mkdirs();
            }
            tts.setLanguage(new Locale(Language.code[position]));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    readTextButton.setVisibility(View.INVISIBLE);
                }
            });
            if(tts.synthesizeToFile(speechText, map, f + "/" + fileID + ".wav") == TextToSpeech.SUCCESS);
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {}

                @Override
                public void onDone(String utteranceId) {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Enable the Read text button
                                readTextButton.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String utteranceId) {}
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

    private class EnglishToTagalog extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        protected void onError(Exception ex) {}

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

    public void translated() {
        String translatetotagalog = mImageDetails.getText().toString();
        String text = translator.translate(translatetotagalog, currentLanguageCode, Language.code[position]);
        mImageDetails = (TextView) findViewById(R.id.image_details);
        mImageDetails.setText(text);
        tts.setLanguage(new Locale(Language.code[position]));
        speechText = text;
        onInitForImage(TextToSpeech.SUCCESS);
    }
}