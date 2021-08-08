package link4.joy.telegram.bot;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import link4.joy.telegram.bot.req.SendMessageRequest;
import link4.joy.telegram.bot.req.SendPhotoRequest;
import link4.joy.telegram.bot.req.SetMyCommandsRequest;
import link4.joy.telegram.bot.res.BaseResponse;
import link4.joy.telegram.bot.res.GetUpdatesResponse;
import link4.joy.telegram.bot.res.SendMessageResponse;
import link4.joy.telegram.bot.type.Update;

public final class TelegramBot {
    private final Map<String, Set<TelegramBotCommand>> commands = new HashMap<>();
    private final String token;
    private final ThreadLocal<ObjectMapper> mapperHolder = new ThreadLocal<ObjectMapper>() {
        @Override
        protected ObjectMapper initialValue() {
            return new ObjectMapper();
        }
    };

    private TelegramBotCommand defaultCommand;
    private long updateOffset = 0;

    public TelegramBot(String token) {
        this.token = token;
    }

    public boolean addCommand(String key, TelegramBotCommand command) {
        if (commands.containsKey(key) == false)
            commands.put(key, new HashSet<TelegramBotCommand>());
        return commands.get(key).add(command);
    }

    public Set<TelegramBotCommand> removeCommand(String key) {
        return commands.remove(key);
    }

    public boolean removeCommand(TelegramBotCommand command) {
        boolean result = false;
        for (Set<TelegramBotCommand> set : commands.values())
            result = set.remove(command) || result;
        return result;
    }

    public boolean removeCommand(String key, TelegramBotCommand command) {
        Set<TelegramBotCommand> set = commands.get(key);
        if (set == null)
            return false;
        return set.remove(command);
    }

    public TelegramBotCommand setDefault(TelegramBotCommand command) {
        TelegramBotCommand old = defaultCommand;
        defaultCommand = command;
        return old;
    }

    private String getRequestUrl(String method) {
        return "https://api.telegram.org/bot" + token + '/' + method;
    }

    private <T> T doGet(String method, Map<String, Object> query, Class<T> returnType) throws IOException {
        String url = getRequestUrl(method);
        if (query != null && query.isEmpty() == false) {
            StringBuilder builder = new StringBuilder(url);
            builder.append('?');
            boolean isFirst = true;
            for (Entry<String, Object> entry : query.entrySet()) {
                builder.append(entry.getKey()).append('=').append(entry.getValue());
                if (isFirst)
                    isFirst = false;
                else
                    builder.append('&');
            }
            url = builder.toString();
        }

        URLConnection connection = new URL(url).openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                builder.append(line);

            String result = builder.toString();
            Logger.getGlobal().log(Level.INFO, method + " <<< " + result);
            return mapperHolder.get().readValue(result, returnType);
        }
    }

    private <T> T doPostJson(String method, String json, Class<T> returnType) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(getRequestUrl(method)).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.connect();

        try (PrintWriter writer = new PrintWriter(connection.getOutputStream())) {
            Logger.getGlobal().log(Level.INFO, method + " >>> " + json);
            writer.append(json);
            writer.flush();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                builder.append(line);

            String result = builder.toString();
            Logger.getGlobal().log(Level.INFO, method + " <<< " + result);
            return mapperHolder.get().readValue(result, returnType);
        }
    }

    private <T> T doPostForm(String method, Map<String, Object> fields, String fileFieldName, File file,
                             Class<T> returnType) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(getRequestUrl(method)).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=----WebKitFormBoundaryT483rRseexWNKfiN");
        connection.setDoOutput(true);
        connection.connect();

        try (OutputStream out = connection.getOutputStream()) {
            out.write("------WebKitFormBoundaryT483rRseexWNKfiN\r\n".getBytes());
            if (fields != null && fields.isEmpty() == false) {
                for (Entry<String, Object> entry : fields.entrySet()) {
                    out.write("Content-Disposition: form-data; name=\"".getBytes());
                    out.write(entry.getKey().getBytes());
                    out.write("\"\r\n\r\n".getBytes());
                    if (CharSequence.class.isAssignableFrom(entry.getValue().getClass()))
                        out.write(((CharSequence) entry.getValue()).toString().getBytes());
                    else if (entry.getValue().getClass().isEnum())
                        out.write(((Enum<?>) entry.getValue()).name().getBytes());
                    else
                        out.write(mapperHolder.get().writeValueAsBytes(entry.getValue()));
                    out.write("\r\n------WebKitFormBoundaryT483rRseexWNKfiN\r\n".getBytes());
                }
            }
            out.write("Content-Disposition: form-data; name=\"".getBytes());
            out.write(fileFieldName.getBytes());
            out.write("\"; filename=\"".getBytes());
            out.write(file.getName().getBytes());
            out.write("\"\r\n\r\n".getBytes());
            try (FileInputStream fin = new FileInputStream(file)) {
                int size = fin.available();
                while (size > 0) {
                    byte[] tmp = new byte[size];
                    fin.read(tmp);
                    out.write(tmp);
                    size = fin.available();
                }
            }
            out.write("\r\n------WebKitFormBoundaryT483rRseexWNKfiN--\r\n".getBytes());
            out.flush();
        }
        Logger.getGlobal().log(Level.INFO, method + " >>> SENT");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                builder.append(line);

            String result = builder.toString();
            Logger.getGlobal().log(Level.INFO, method + " >>> " + result);
            return mapperHolder.get().readValue(result, returnType);
        }
    }

    public List<BaseResponse> processLastUpdate() throws IOException {
        GetUpdatesResponse res1 = getUpdates();
        if (res1.ok && res1.result != null && res1.result.length > 0) {
            Update update = res1.result[res1.result.length - 1];
            Set<TelegramBotCommand> set = commands.get(update.message.text.contains(" ")
                    ? update.message.text.substring(0, update.message.text.indexOf(' '))
                    : update.message.text);

            if (set == null || set.isEmpty()) {
                if (defaultCommand == null)
                    return Arrays.asList(BaseResponse.NO_HANDLER);
                return Arrays.asList(defaultCommand.process(this, update));
            }
            List<BaseResponse> res2 = new ArrayList<>();
            for (TelegramBotCommand command : set)
                res2.add(command.process(this, update));
            return res2;
        } else
            return Arrays.asList(BaseResponse.NOTHING_TO_DO);
    }

    public GetUpdatesResponse getUpdates() throws IOException {
        return getUpdates(updateOffset);
    }

    public GetUpdatesResponse getUpdates(long offset) throws IOException {
        Map<String, Object> query = new HashMap<>();
        query.put("offset", offset);
        GetUpdatesResponse res = doGet("getUpdates", query, GetUpdatesResponse.class);
        if (res.ok && res.result != null && res.result.length > 0)
            updateOffset = res.result[res.result.length - 1].updateId + 1;
        return res;
    }

    public SendMessageResponse sendMessage(SendMessageRequest req) throws IOException {
        return doPostJson("sendMessage", mapperHolder.get().writeValueAsString(req), SendMessageResponse.class);
    }

    public SendMessageResponse sendPhoto(SendPhotoRequest req) throws IOException {
        Map<String, Object> fields = new HashMap<>();
        fields.put("chat_id", req.chatId);
        if (req.caption != null)
            fields.put("caption", req.caption);
        if (req.parseMode != null)
            fields.put("parse_mode", req.parseMode);
        if (req.replayToMessageId != null)
            fields.put("replay_to_message_id", req.replayToMessageId);
        if (req.allowSendingWithoutReply != null)
            fields.put("allow_sending_without_reply", req.allowSendingWithoutReply);
        if (req.replyMarkup != null)
            fields.put("reply_markup", req.replyMarkup);
        return doPostForm("sendPhoto", fields, "photo", req.photo, SendMessageResponse.class);
    }

    public BaseResponse setMyCommands(SetMyCommandsRequest req) throws IOException {
        return doPostJson("setMyCommands", mapperHolder.get().writeValueAsString(req), BaseResponse.class);
    }
}
