package net.my.videostreamingplayer.Utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import net.my.videostreamingplayer.App.MyApp;

public interface ToastUtil {
    static void showToastMessageShort(String message) {
        showToastMessage(message, false);
    }


    static void showToastMessage(String message) {
        showToastMessage(message, true);
    }

    static void showToastMessage(String message, boolean isLongTime) {

        if (message.length() == 0) {
            return;
        }

        int showTime = Toast.LENGTH_SHORT;
        if (isLongTime) {
            showTime = Toast.LENGTH_LONG;
        }

        int finalShowTime = showTime;
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(MyApp.getAppContext(), message, finalShowTime).show());
    }
}
