package com.textfield.json.ottawastreetcameras;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.textfield.json.ottawastreetcameras.activities.AlternateMainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<AlternateMainActivity> mActivityRule = new ActivityTestRule<>(AlternateMainActivity.class);

    @Test
    public void testListSelection() {

        for (int i = 0; i < 1000; i++) {
            int p = (int) (Math.random() * (mActivityRule.getActivity().getCameras().size() - 1));
            mActivityRule.getActivity().selectCamera(mActivityRule.getActivity().getCameras().get(p));
            assert mActivityRule.getActivity().getSelectedCameras().size() <= mActivityRule.getActivity().getMaxCameras();
        }
        mActivityRule.getActivity().getViewSwitcher().showNext();
    }
}

