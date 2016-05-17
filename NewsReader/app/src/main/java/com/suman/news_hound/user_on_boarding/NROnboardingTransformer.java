package com.suman.news_hound.user_on_boarding;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by sumansucharitdas on 5/13/16.
 */
public class NROnboardingTransformer implements ViewPager.PageTransformer {
    public void transformPage(View view, float position) {

        if (position <= -1) {
            // [-00,-1): the page is way off-screen to the left.
            if(NROnboardingActivity.curItem == 3){
                NROnboardingActivity.scrolledEnded = true;
            }
            else {
                NROnboardingActivity.scrolledEnded = false;
            }
        } else if (position <= 1) {
            // [-1,1]: the page is "centered"
            NROnboardingActivity.scrolledEnded = false;
        } else {
            // (1,+00]: the page is way off-screen to the right.
            NROnboardingActivity.scrolledEnded = false;
        }
    }
}