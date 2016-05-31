package com.suman.news_hound.user_on_boarding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.suman.news_hound.R;
import com.suman.news_hound.activities.FirstPageActionActivity;
import com.suman.news_hound.activities.NRMainActivity;

/**
 * @author sumansucharitdas
 */
public class NROnboardingActivity extends AppCompatActivity {
    private SectionsPagerAdapter        mSectionsPagerAdapter;
    private static final String         PREFERENCES_FILE = "materialsample_settings";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager                   mViewPager;
    private Button                      mFinishBtn;
    private CoordinatorLayout           mCoordinator;
    private ImageView[] indicators;
    private ImageView mainPage, snapPage, ocrPage, ttsPage;
    private static ImageView sectionImage;

    private static final String         TAG = "PagerActivity";
    private int                         page = 0;   //  to track page position
    public static boolean scrolledEnded = false;
    public static int curItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mainPage = (ImageView) findViewById(R.id.intro_main_page);
        snapPage = (ImageView) findViewById(R.id.intro_snap);
        ocrPage = (ImageView) findViewById(R.id.intro_ocr);
        ttsPage = (ImageView) findViewById(R.id.intro_tts);
        indicators = new ImageView[]{mainPage, snapPage, ocrPage, ttsPage};
        indicators[0].setBackgroundResource(R.drawable.indicator_selected);

        mFinishBtn = (Button) findViewById(R.id.intro_btn_finish);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.main_content);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(page);
        mViewPager.setPageTransformer(true, new NROnboardingTransformer());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private boolean enabled;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mViewPager.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOnboardingBackground));
            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                mViewPager.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                updateIndicators(page);
                mFinishBtn.setVisibility(position == 3 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int lastIdx = mSectionsPagerAdapter.getCount() - 1;
                curItem = mViewPager.getCurrentItem();
                if (curItem == lastIdx && state == 1 && scrolledEnded) {
                    finish();
                    saveSharedSetting(NROnboardingActivity.this, FirstPageActionActivity.PREF_USER_FIRST_TIME, "false");
                    Intent imageCapture = new Intent(getApplicationContext(), NRMainActivity.class);
                    startActivity(imageCapture);
                }
            }
        });

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                saveSharedSetting(NROnboardingActivity.this, FirstPageActionActivity.PREF_USER_FIRST_TIME, "false");
                Intent imageCapture = new Intent(getApplicationContext(), FirstPageActionActivity.class);
                startActivity(imageCapture);
            }
        });
    }

    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        int[] bgs = new int[]{R.mipmap.ic_onboarding_main, R.mipmap.ic_onboarding_snap,
                R.mipmap.ic_onboarding_ocr, R.mipmap.ic_onboarding_tts};

        public PlaceholderFragment() {}

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
            TextView sectionHeader = (TextView) rootView.findViewById(R.id.section_label);
            sectionHeader.setText(getSectionTitle(getArguments().getInt(ARG_SECTION_NUMBER) - 1));
            TextView sectionDetail = (TextView) rootView.findViewById(R.id.section_detail);
            sectionDetail.setText(getSectionDetail(getArguments().getInt(ARG_SECTION_NUMBER) - 1));
            sectionImage = (ImageView) rootView.findViewById(R.id.section_img);
            sectionImage.setBackgroundResource(bgs[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            sectionImage.requestLayout();
            sectionImage.getLayoutParams().height = 3 * height/ 5;
            sectionImage.getLayoutParams().width = width;
            return rootView;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Your Pocket Assistant";
                case 1:
                    return "1. Snap It";
                case 2:
                    return "2. Automatic Text Recognition";
                case 3:
                    return "3. Text to Speech";
            }
            return null;
        }
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    private static CharSequence getSectionTitle(int position) {
        switch (position) {
            case 0:
                return "Your Pocket Assistant";
            case 1:
                return "1. Snap It";
            case 2:
                return "2. Automatic Text Recognition";
            case 3:
                return "3. Text to Speech";
        }
        return "";
    }

    private static CharSequence getSectionDetail(int position) {
        switch (position) {
            case 0:
                return "Snap images of documents, newspapers, whiteboards and convert them " +
                        "into readable text/speech.";
            case 1:
                return "Try snapping newspapers, documents, whiteboards, or anything with " +
                        "text on it.";
            case 2:
                return "Once uploaded, our intelligent OCR technology translates the image " +
                        "to readable text.";
            case 3:
                return "Google's patented Text to Speech will read out to you the text, in a " +
                        "friendly casual tone!";
        }
        return "";
    }
}