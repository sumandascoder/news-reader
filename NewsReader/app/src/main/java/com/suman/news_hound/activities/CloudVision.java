package com.suman.news_hound.activities;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sumansucharitdas on 7/16/16.
 */
public class CloudVision extends AsyncTask<Object, Integer, String> {

    int max = 0;
    public NRMainActivity localContext;
    private String CLOUD_VISION_API_KEY;
    private final String     TAG = CloudVision.class.getSimpleName();

    public CloudVision(NRMainActivity localCtxt, String api_key){
        this.localContext = localCtxt;
        CLOUD_VISION_API_KEY = api_key;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        NRMainActivity.progressBarImageExtract.setVisibility(View.VISIBLE);
        NRMainActivity.progressBarImageExtract.setProgress(0);
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            localContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NRMainActivity.readFABButton.setVisibility(View.INVISIBLE);
                    NRMainActivity.progressStatusText.setText("");
                    NRMainActivity.imageDetailsText.setText("");
                }
            });
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

                NRMainActivity.bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                NRMainActivity.progressBarImageExtract.setMax(imageBytes.length);
                max = imageBytes.length;

                // Keep it going till 9/10th so that value on progress bar proceeds upto a point
                for (int i = 0 ; i < imageBytes.length * 9/10; i = i + (imageBytes.length*1/10)) {
                    publishProgress(i);
                }
                // Base64 encode the JPEG
                base64EncodedImage.encodeContent(imageBytes);
                annotateImageRequest.setImage(base64EncodedImage);

                // Add the features we want
                annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                    Feature textDetection = new Feature();
                    textDetection.setType("TEXT_DETECTION").setMaxResults(10);
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
            Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
        }
        return "API failed to return results";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        NRMainActivity.readFABButton.setVisibility(View.INVISIBLE);
        if(values[0] < max){
            NRMainActivity.progressStatusText.setText("Uploading ... ");
            NRMainActivity.progressBarImageExtract.setProgress(values[0]);
        }
        if (values[0] > max/2){
            NRMainActivity.progressStatusText.setText("Extracting the text ... Sit tight!");
            NRMainActivity.imageStatusText.setVisibility(View.VISIBLE);
            if(values[0] > 8 * max/10){
                NRMainActivity.imageStatusText.setText("Extracted Text from Image:");
                ViewGroup.LayoutParams layoutParams = localContext.blurImage.getLayoutParams();
                final float scale = localContext.getResources().getDisplayMetrics().density;
                int dpHeightInPx = (int) (140 * scale);
                NRMainActivity.blurImage.getLayoutParams().height = dpHeightInPx;
                NRMainActivity.blurImage.setLayoutParams(layoutParams);
                NRMainActivity.blurImage.setVisibility(View.VISIBLE);
            }
        }
        else if((values[0] == max/2)){
            NRMainActivity.progressStatusText.setText("Uploading ... 50%");
            NRMainActivity.imageStatusText.setVisibility(View.INVISIBLE);
        }
        if (values[0] < max/2){
            NRMainActivity.imageStatusText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        NRMainActivity.progressBarImageExtract.setProgress(max);
        NRMainActivity.progressStatusText.setText("Auto saved to News Library. Dated: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date()));
        NRMainActivity.imageStatusText.setText("Extracted text from Image:");
        NRMainActivity.imageDetailsText.setText(result);
        NRMainActivity.blurImage.getLayoutParams().height = 0;
        NRMainActivity.blurImage.setVisibility(View.INVISIBLE);
        NRMainActivity.progressBarImageExtract.setVisibility(View.INVISIBLE);
    }

    // Extract the string from the Google OCR service
    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";
        List<EntityAnnotation> textAnnotations = response.getResponses().get(0).getTextAnnotations();
        Log.i("Response", response.toString() + "");
        if (textAnnotations != null) {
            NRMainActivity.speechText = "";
            NRMainActivity.speechText += String.format("%s", textAnnotations.get(0).getDescription());
            NRMainActivity.currentLanguageCode = textAnnotations.get(0).getLocale();
            message += String.format("%s", textAnnotations.get(0).getDescription());
            localContext.initSpeech();
        } else {
            NRMainActivity.speechText = "I found no text";
            message += "No text";
        }
        return message;
    }
}
