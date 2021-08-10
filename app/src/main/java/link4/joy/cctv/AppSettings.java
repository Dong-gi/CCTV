package link4.joy.cctv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.PowerManager;

import com.pedro.rtmp.utils.ConnectCheckerRtmp;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import link4.joy.cctv.entity.AbstractAppState;
import link4.joy.cctv.entity.AppState;
import link4.joy.cctv.entity.FlashState;
import link4.joy.cctv.entity.TelegramBotCommands;
import link4.joy.cctv.entity.YoutubeState;
import link4.joy.telegram.bot.TelegramBot;
import link4.joy.telegram.bot.consts.ParseMode;
import link4.joy.telegram.bot.req.SendMessageRequest;
import link4.joy.telegram.bot.req.SetMyCommandsRequest;
import link4.joy.telegram.bot.type.BotCommand;

import static android.content.Context.POWER_SERVICE;

public class AppSettings {
    public static final String TAG = "AppSettings";

    private static RtmpCamera1 camera;
    private static int cameraAngle;
    private static int cameraKBitRate = 9000;
    private static Context context;
    private static AppState<FlashState> flashState = new AbstractAppState<FlashState>("Flash") {
        private Camera camera;

        @Override
        public void setDesiredState(FlashState desiredState) {
            super.setDesiredState(desiredState);
            SharedPreferences.Editor editor = context.getSharedPreferences(getString(R.string.setting_key), Context.MODE_PRIVATE).edit();
            editor.putString(getString(R.string.setting_key_flash_on_off), desiredState.name());
            editor.commit();
        }

        @Override
        public void adjustState() {
            switch (getDesiredState()) {
                case OFF:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        CameraManager camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                        try {
                            String cameraId = camManager.getCameraIdList()[0];
                            camManager.setTorchMode(cameraId, false);
                            setCurrentState(FlashState.OFF);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (camera != null) {
                            camera.stopPreview();
                            camera.release();
                        }
                    }
                    return;
                case ON:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        CameraManager camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                        try {
                            String cameraId = camManager.getCameraIdList()[0];
                            camManager.setTorchMode(cameraId, true);
                            setCurrentState(FlashState.ON);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        camera = Camera.open();
                        Camera.Parameters p = camera.getParameters();
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(p);
                        camera.startPreview();
                    }
                    return;
                default:
                    throw new IllegalStateException("Unexpected value: " + getDesiredState());
            }
        }
    };
    private static boolean isInitialized;
    private static TelegramBot telegramBot;
    private static long telegramChatId;
    private static String telegramToken;
    private static String youtubeRtmpUrl;
    private static AppState<YoutubeState> youtubeState = new AbstractAppState<YoutubeState>("YouTube") {
        private PowerManager.WakeLock wakeLock;

        @Override
        public void setDesiredState(YoutubeState desiredState) {
            SharedPreferences.Editor editor = context.getSharedPreferences(getString(R.string.setting_key), Context.MODE_PRIVATE).edit();
            editor.putString(getString(R.string.setting_key_youtube_on_off), desiredState.name());
            editor.commit();
            super.setDesiredState(desiredState);
        }

        @Override
        public void adjustState() {
            if (getCurrentState() == getDesiredState())
                return;
            if (getCurrentState() == YoutubeState.URL_NOT_SET) {
                sendBotMessage("Plz set YouTube RTMP key first.\n\nYou can set YouTube by typing\n\n<strong>/set_youtube xxxx-xxxx-xxxx-xxxx-xxxx</strong>\n\nHere 'xxxx...xxxx' is the RTMP key which you can find in <a href=\"https://studio.youtube.com/\">https://studio.youtube.com/</a>");
                return;
            }
            switch (getDesiredState()) {
                case OFF:
                    camera.stopStream();
                    wakeLock.release();
                    return;
                case ON:
                    if (wakeLock == null) {
                        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
                        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                                "CCTV:Streaming");
                    }
                    wakeLock.acquire();
                    camera.startStream("rtmp://a.rtmp.youtube.com/live2/" + youtubeRtmpUrl);
                    return;
            }
        }
    };

    public static RtmpCamera1 getCamera() {
        return camera;
    }

    public static Context getContext() {
        return context;
    }

    public static AppState<FlashState> getFlashState() {
        return flashState;
    }

    public static long getTelegramChatId() {
        return telegramChatId;
    }

    public static String getYoutubeRtmpUrl() {
        return youtubeRtmpUrl;
    }

    public static AppState<YoutubeState> getYoutubeState() {
        return youtubeState;
    }

    public static void setCameraAngle(int angle) {
        AppSettings.cameraAngle = angle;
        SharedPreferences.Editor editor = context.getSharedPreferences(getString(R.string.setting_key), Context.MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.setting_key_camera_angle), angle);
        editor.commit();
    }

    public static void setCameraKBitRate(int kBitRate) {
        AppSettings.cameraKBitRate = kBitRate;
        SharedPreferences.Editor editor = context.getSharedPreferences(getString(R.string.setting_key), Context.MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.setting_key_camera_kbit_rate), kBitRate);
        editor.commit();
    }

    public static void setTelegramChatId(long telegramChatId) {
        if (telegramChatId < 1)
            return;
        AppSettings.telegramChatId = telegramChatId;
        SharedPreferences.Editor editor = context.getSharedPreferences(getString(R.string.setting_key), Context.MODE_PRIVATE).edit();
        editor.putLong(getString(R.string.setting_key_telegram_chat_id), telegramChatId);
        editor.commit();
    }

    public static void setYoutubeRtmpUrl(String youtubeRtmpUrl) {
        String oldUrl = AppSettings.youtubeRtmpUrl;
        AppSettings.youtubeRtmpUrl = youtubeRtmpUrl;
        SharedPreferences.Editor editor = context.getSharedPreferences(getString(R.string.setting_key), Context.MODE_PRIVATE).edit();
        editor.putString(getString(R.string.setting_key_youtube_rtmp_url), youtubeRtmpUrl);
        editor.commit();

        if (Objects.equals(oldUrl, youtubeRtmpUrl) == false) {
            Intent mStartActivity = new Intent(context, MainActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);
        }
    }

    public static void init(Context context) {
        if (context == null || isInitialized)
            return;
        AppSettings.context = context;
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.setting_key), Context.MODE_PRIVATE);
        telegramToken = preferences.getString(context.getString(R.string.setting_key_telegram_token), telegramToken);
        if (telegramToken == null || telegramToken.length() < 1)
            return;

        if (telegramBot == null) {
            telegramBot = new TelegramBot(telegramToken);
            telegramChatId = preferences.getLong(context.getString(R.string.setting_key_telegram_chat_id), telegramChatId);
            telegramBot.setDefault(TelegramBotCommands.DEFAULT);

            if (telegramChatId > 0) {
                SetMyCommandsRequest req1 = new SetMyCommandsRequest();
                for (TelegramBotCommands command : TelegramBotCommands.getCommands()) {
                    telegramBot.addCommand(command.key, command);
                    BotCommand c = new BotCommand();
                    c.command = command.command;
                    c.description = command.description();
                    req1.commands.add(c);
                }
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            telegramBot.setMyCommands(req1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
                try {
                    telegramBot.processLastUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, 0, 5, TimeUnit.SECONDS);
        }

        if (camera == null) {
            camera = new RtmpCamera1(context.getApplicationContext(), new ConnectCheckerRtmp() {
                @Override
                public void onConnectionStartedRtmp(String s) {
                    sendBotMessage("YouTube Connection Start...");
                }

                @Override
                public void onConnectionSuccessRtmp() {
                    sendBotMessage("YouTube Connection Succeed...\nPlz keep <strong>screen on</strong> if you use <strong>Android 9 ↑</strong>");
                    youtubeState.setCurrentState(YoutubeState.ON);
                }

                @Override
                public void onConnectionFailedRtmp(String s) {
                    sendBotMessage("YouTube Connection Failed... Check your url : " + s);
                }

                @Override
                public void onNewBitrateRtmp(long l) {
                }

                @Override
                public void onDisconnectRtmp() {
                    sendBotMessage("YouTube Connection Ended...");
                    youtubeState.setCurrentState(YoutubeState.OFF);

                    Intent mStartActivity = new Intent(context, MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    System.exit(0);
                }

                @Override
                public void onAuthErrorRtmp() {
                }

                @Override
                public void onAuthSuccessRtmp() {
                }
            });
            if (camera.isAutoFocusEnabled())
                camera.enableAutoFocus();
            camera.prepareVideo(1920,
                    1080,
                    24,
                    preferences.getInt(context.getString(R.string.setting_key_camera_kbit_rate), cameraKBitRate) * 1024,
                    preferences.getInt(context.getString(R.string.setting_key_camera_angle), cameraAngle));
            camera.prepareAudio();
        }

        youtubeRtmpUrl = preferences.getString(context.getString(R.string.setting_key_youtube_rtmp_url), youtubeRtmpUrl);
        if (youtubeRtmpUrl == null) {
            youtubeState.setCurrentState(YoutubeState.URL_NOT_SET);
        } else {
            youtubeState.setCurrentState(YoutubeState.OFF);
            youtubeState.setDesiredState(YoutubeState.valueOf(preferences.getString(context.getString(R.string.setting_key_youtube_on_off), YoutubeState.OFF.name())));
            flashState.setDesiredState(FlashState.valueOf(preferences.getString(context.getString(R.string.setting_key_flash_on_off), FlashState.OFF.name())));
        }

        isInitialized = true;
    }

    public static String getString(int id) {
        if (context == null)
            return null;
        return context.getString(id);
    }

    public static void sendBotMessage(String message) {
        if (telegramBot == null || telegramChatId < 1)
            return;
        SendMessageRequest req = new SendMessageRequest();
        req.chatId = telegramChatId;
        req.text = message;
        req.parseMode = ParseMode.HTML;
        new Thread() {
            @Override
            public void run() {
                try {
                    telegramBot.sendMessage(req);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
