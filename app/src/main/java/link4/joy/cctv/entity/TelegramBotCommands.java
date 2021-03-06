package link4.joy.cctv.entity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import link4.joy.cctv.AppSettings;
import link4.joy.cctv.R;
import link4.joy.telegram.bot.TelegramBot;
import link4.joy.telegram.bot.TelegramBotCommand;
import link4.joy.telegram.bot.consts.ParseMode;
import link4.joy.telegram.bot.req.SendMessageRequest;
import link4.joy.telegram.bot.req.SetMyCommandsRequest;
import link4.joy.telegram.bot.res.SendMessageResponse;
import link4.joy.telegram.bot.type.BotCommand;
import link4.joy.telegram.bot.type.Update;

public enum TelegramBotCommands implements TelegramBotCommand {
    DEFAULT {
        @Override
        public SendMessageResponse process(TelegramBot bot, Update update) throws IOException {
            AppSettings.setTelegramChatId(update.message.chat.id);

            SetMyCommandsRequest req1 = new SetMyCommandsRequest();
            for (TelegramBotCommands command : getCommands()) {
                bot.addCommand(command.key, command);
                BotCommand c = new BotCommand();
                c.command = command.command;
                c.description = command.description();
                req1.commands.add(c);
            }
            bot.setMyCommands(req1);

            if (AppSettings.getYoutubeRtmpUrl() == null) {
                SendMessageRequest req2 = new SendMessageRequest();
                req2.chatId = update.message.chat.id;
                req2.text = "You can set YouTube by typing\n\n<strong>/set_youtube xxxx-xxxx-xxxx-xxxx-xxxx</strong>\n\nHere 'xxxx...xxxx' is the RTMP key which you can find in <a href=\"https://studio.youtube.com/\">https://studio.youtube.com/</a>";
                req2.parseMode = ParseMode.HTML;
                bot.sendMessage(req2);
            } else {
                SendMessageRequest req2 = new SendMessageRequest();
                req2.chatId = update.message.chat.id;
                StringBuilder builder = new StringBuilder("<strong>Available commands</strong>\n\n");
                for (TelegramBotCommands command : getCommands())
                    builder.append(command.key).append(" : ").append(command.description()).append("\n\n");
                req2.text = builder.toString();
                req2.parseMode = ParseMode.HTML;
                bot.sendMessage(req2);
            }

            return null;
        }
    },
    BATTERY {
        @Override
        public String description() {
            return AppSettings.getString(R.string.command_description_battery);
        }

        @Override
        public SendMessageResponse process(TelegramBot bot, Update update) throws IOException {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = AppSettings.getContext().registerReceiver(null, ifilter);

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level * 100 / (float) scale;

            SendMessageRequest req = new SendMessageRequest();
            req.chatId = update.message.chat.id;
            req.text = "Battery : " + batteryPct + '%';
            SendMessageResponse res = bot.sendMessage(req);
            return res;
        }
    },
    FLIP_CAMERA {
        @Override
        public String description() {
            return AppSettings.getString(R.string.command_description_flip_camera);
        }

        @Override
        public SendMessageResponse process(TelegramBot bot, Update update) throws IOException {
            SendMessageRequest req = new SendMessageRequest();
            req.chatId = update.message.chat.id;
            req.text = "Try to flip camera...";
            SendMessageResponse res = bot.sendMessage(req);
            AppSettings.getCamera().switchCamera();
            return res;
        }
    },
    ON_FLASH {
        @Override
        public String description() {
            return AppSettings.getString(R.string.command_description_on_flash);
        }

        @Override
        public SendMessageResponse process(TelegramBot bot, Update update) throws IOException {
            if (AppSettings.getFlashState().getCurrentState() == FlashState.ON) {
                SendMessageRequest req = new SendMessageRequest();
                req.chatId = update.message.chat.id;
                req.text = "Flash light already turned on...";
                SendMessageResponse res = bot.sendMessage(req);
                return res;
            }

            SendMessageRequest req = new SendMessageRequest();
            req.chatId = update.message.chat.id;
            req.text = "Try to turn on flash light...";
            SendMessageResponse res = bot.sendMessage(req);
            AppSettings.getFlashState().setDesiredState(FlashState.ON);
            return res;
        }
    },
    OFF_FLASH {
        @Override
        public String description() {
            return AppSettings.getString(R.string.command_description_off_flash);
        }

        @Override
        public SendMessageResponse process(TelegramBot bot, Update update) throws IOException {
            if (AppSettings.getFlashState().getCurrentState() == FlashState.OFF) {
                SendMessageRequest req = new SendMessageRequest();
                req.chatId = update.message.chat.id;
                req.text = "Flash light already turned off...";
                SendMessageResponse res = bot.sendMessage(req);
                return res;
            }

            SendMessageRequest req = new SendMessageRequest();
            req.chatId = update.message.chat.id;
            req.text = "Try to turn off flash light...";
            SendMessageResponse res = bot.sendMessage(req);
            AppSettings.getFlashState().setDesiredState(FlashState.OFF);
            return res;
        }
    },
    ON_LIVE {
        @Override
        public String description() {
            return AppSettings.getString(R.string.command_description_on_live);
        }

        @Override
        public SendMessageResponse process(TelegramBot bot, Update update) throws IOException {
            if (AppSettings.getYoutubeState().getCurrentState() == YoutubeState.ON) {
                SendMessageRequest req = new SendMessageRequest();
                req.chatId = update.message.chat.id;
                req.text = "Live is ongoing...";
                SendMessageResponse res = bot.sendMessage(req);
                return res;
            }

            SendMessageRequest req = new SendMessageRequest();
            req.chatId = update.message.chat.id;
            req.text = "Try to turn on YouTube live streaming...";
            SendMessageResponse res = bot.sendMessage(req);
            AppSettings.getYoutubeState().setDesiredState(YoutubeState.ON);
            return res;
        }
    },
    OFF_LIVE {
        @Override
        public String description() {
            return AppSettings.getString(R.string.command_description_off_live);
        }

        @Override
        public SendMessageResponse process(TelegramBot bot, Update update) throws IOException {
            if (AppSettings.getYoutubeState().getCurrentState() == YoutubeState.OFF) {
                SendMessageRequest req = new SendMessageRequest();
                req.chatId = update.message.chat.id;
                req.text = "Live already ended...";
                SendMessageResponse res = bot.sendMessage(req);
                return res;
            }

            SendMessageRequest req = new SendMessageRequest();
            req.chatId = update.message.chat.id;
            req.text = "Try to turn off YouTube live streaming\nApp will restart once to release resources...";
            SendMessageResponse res = bot.sendMessage(req);
            AppSettings.getYoutubeState().setDesiredState(YoutubeState.OFF);
            return res;
        }
    },
    SET_CAMERA_ANGLE {
        @Override
        public String description() {
            return AppSettings.getString(R.string.command_description_set_angle);
        }

        @Override
        public SendMessageResponse process(TelegramBot bot, Update update) throws IOException {
            if (update.message.text.contains(" ") == false) {
                SendMessageRequest req = new SendMessageRequest();
                req.chatId = update.message.chat.id;
                req.parseMode = ParseMode.HTML;
                req.text = "Plz check spelling. The syntax is\n\n<strong>/set_camera_angle 180</strong>";
                return bot.sendMessage(req);
            }

            int angle = Integer.valueOf(update.message.text.substring(update.message.text.indexOf(' ')).replaceAll("\\s", ""));
            AppSettings.setCameraAngle(angle);
            SendMessageRequest req = new SendMessageRequest();
            req.chatId = update.message.chat.id;
            req.text = "Camera angle was set(" + angle + "). This will be applied from next LIVE";
            return bot.sendMessage(req);
        }
    },
    SET_CAMERA_KBIT_RATE {
        @Override
        public String description() {
            return AppSettings.getString(R.string.command_description_set_kbit_rate);
        }

        @Override
        public SendMessageResponse process(TelegramBot bot, Update update) throws IOException {
            if (update.message.text.contains(" ") == false) {
                SendMessageRequest req = new SendMessageRequest();
                req.chatId = update.message.chat.id;
                req.parseMode = ParseMode.HTML;
                req.text = "Plz check spelling. The syntax is\n\n<strong>/set_kbit_rate 5000</strong>";
                return bot.sendMessage(req);
            }

            int kbitRate = Integer.valueOf(update.message.text.substring(update.message.text.indexOf(' ')).replaceAll("\\s", ""));
            AppSettings.setCameraKBitRate(kbitRate);
            SendMessageRequest req = new SendMessageRequest();
            req.chatId = update.message.chat.id;
            req.text = "Camera Kb rate was set(" + kbitRate + "). This will be applied from next LIVE";
            return bot.sendMessage(req);
        }
    },
    SET_YOUTUBE {
        @Override
        public String description() {
            return AppSettings.getString(R.string.command_description_set_youtube);
        }

        @Override
        public SendMessageResponse process(TelegramBot bot, Update update) throws IOException {
            if (update.message.text.contains(" ") == false) {
                SendMessageRequest req = new SendMessageRequest();
                req.chatId = update.message.chat.id;
                req.parseMode = ParseMode.HTML;
                req.text = "Plz check spelling. The syntax is\n\n<strong>/set_youtube xxxx-xxxx-xxxx-xxxx-xxxx</strong>\n\nHere 'xxxx...xxxx' is the RTMP key which you can find in <a href=\"https://studio.youtube.com/\">https://studio.youtube.com/</a>";
                return bot.sendMessage(req);
            }

            String rtmpKey = update.message.text.substring(update.message.text.indexOf(' ')).replaceAll("\\s", "");
            AppSettings.setYoutubeRtmpUrl(rtmpKey);
            SendMessageRequest req = new SendMessageRequest();
            req.chatId = update.message.chat.id;
            req.parseMode = ParseMode.HTML;
            req.text = "RTMP key was set(" + rtmpKey + "). Now you can try <strong>/on_live</strong>";
            return bot.sendMessage(req);
        }
    };

    public final String command;
    public final String key;

    {
        command = name().toLowerCase();
        key = '/' + command;
    }

    public static List<TelegramBotCommands> getCommands() {
        List<TelegramBotCommands> commands = new LinkedList<>();
        commands.addAll(Arrays.asList(ON_LIVE, OFF_LIVE, SET_YOUTUBE, ON_FLASH, OFF_FLASH, FLIP_CAMERA, SET_CAMERA_ANGLE, SET_CAMERA_KBIT_RATE, BATTERY));
        return commands;
    }

    @Override
    public String command() {
        return command;
    }

    @Override
    public String description() {
        return command;
    }

    @Override
    public SendMessageResponse process(TelegramBot bot, Update update) throws IOException {
        SendMessageRequest req = new SendMessageRequest();
        req.chatId = update.message.chat.id;
        req.text = "There's no handler implementation for the message;" + update.message.text;
        return bot.sendMessage(req);
    }
}
