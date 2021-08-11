package link4.joy.cctv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import java.io.IOException;

import link4.joy.telegram.bot.TelegramBot;

public class CheckTelegramService extends Service {
    private static Intent reuseSelfIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int r = super.onStartCommand(intent, flags, startId);

        if (reuseSelfIntent == null) {
            synchronized (CheckTelegramService.class) {
                if (reuseSelfIntent == null)
                    reuseSelfIntent = new Intent(this, CheckTelegramService.class);
            }
        }

        PendingIntent pendingIntent = PendingIntent.getService(this, AppSettings.INTENT_ID_START_CHECK_TELEGRAM_SERVICE, reuseSelfIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        } else {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        }

        new Thread(() -> {
            try {
                TelegramBot bot = AppSettings.getTelegramBot();
                if (bot == null)
                    return;
                bot.processLastUpdate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        return r;
    }
}