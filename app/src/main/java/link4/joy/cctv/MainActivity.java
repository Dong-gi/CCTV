package link4.joy.cctv;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionChecker.check(this);
        AppSettings.init(this);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.setting_key), Context.MODE_PRIVATE);
        long chatId = preferences.getLong(getString(R.string.setting_key_telegram_chat_id), 0);
        if (chatId > 0) {
            findViewById(R.id.textView).setVisibility(View.GONE);
            findViewById(R.id.textView3).setVisibility(View.GONE);
            findViewById(R.id.editTextTelegramToken).setVisibility(View.GONE);
            findViewById(R.id.buttonSetTelegramToken).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionChecker.REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission rejected", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                return;
        }
        PermissionChecker.check(this);
    }

    public void onApplyToken(View v) {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.setting_key), Context.MODE_PRIVATE).edit();
        editor.putString(getString(R.string.setting_key_telegram_token), ((EditText) findViewById(R.id.editTextTelegramToken)).getText().toString());
        editor.commit();
        AppSettings.init(this);
    }
}