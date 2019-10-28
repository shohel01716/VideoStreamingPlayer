package net.my.videostreamingplayer;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PlayerActivityTest {
    @Rule
    public ActivityTestRule<PlayerActivity> mainActivityActivityTestRule = new ActivityTestRule<>(PlayerActivity.class);

    private PlayerActivity playerActivity = null;
    private Bundle savedInstanceState;

    @Before
    public void setUp() throws Exception {
        playerActivity = mainActivityActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        playerActivity = null;
    }

    @Test
    public void setRewind() {
        playerActivity.setRewind();
    }


    @Test
    public void setForward() {
        playerActivity.setForward();
    }

}