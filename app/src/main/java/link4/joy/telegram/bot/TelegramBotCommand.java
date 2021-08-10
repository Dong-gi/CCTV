package link4.joy.telegram.bot;

import java.io.IOException;

import link4.joy.telegram.bot.res.BaseResponse;
import link4.joy.telegram.bot.type.Update;

public interface TelegramBotCommand {
    String command();

    String description();

    <X extends BaseResponse> BaseResponse process(TelegramBot bot, Update update) throws IOException;
}
