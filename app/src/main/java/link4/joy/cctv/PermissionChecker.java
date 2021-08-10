package link4.joy.cctv;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public final class PermissionChecker {
    public static final int REQUEST_CODE = 0;

    public static void check(MainActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
            return;

        new AlertDialog.Builder(activity)
                .setMessage("App needs permissions(camera + voice) to record.\n\nSince all settings will be set via Telegram except bot token, we request permissions here.")
                .setPositiveButton("OK", (x, y) -> requestPermissions(activity))
                .setOnCancelListener((x) -> requestPermissions(activity))
                .create()
                .show();
    }

    private static void requestPermissions(MainActivity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                REQUEST_CODE
        );
    }
}
