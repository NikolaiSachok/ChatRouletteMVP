package com.video.chat.v.love.chatroulette.presentation.makeselfie;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.video.chat.v.love.chatroulette.R;

public class PermissionDeligate {

        private static final int REQUEST_CODE = 10;
        private final Activity activity;

    PermissionDeligate(Activity activity) {
        this.activity = activity;
    }

        boolean hasCameraPermission() {
        int permissionCheckResult = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
        );
        return permissionCheckResult == PackageManager.PERMISSION_GRANTED;
    }

        void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CODE
        );
    }

        boolean resultGranted(int requestCode,
        String[] permissions,
        int[] grantResults) {

        if (requestCode != REQUEST_CODE) {
            return false;
        }

        if (grantResults.length < 1) {
            return false;
        }
        if (!(permissions[0].equals(Manifest.permission.CAMERA))) {
            return false;
        }

        View noPermissionView = activity.findViewById(R.id.no_permission);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            noPermissionView.setVisibility(View.GONE);
            return true;
        }

        requestCameraPermission();
        noPermissionView.setVisibility(View.VISIBLE);
        return false;
    }
}
