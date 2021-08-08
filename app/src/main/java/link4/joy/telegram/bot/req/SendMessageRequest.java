package link4.joy.telegram.bot.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import link4.joy.telegram.bot.consts.ParseMode;
import link4.joy.telegram.bot.type.ReplyMarkup;

@JsonInclude(Include.NON_NULL)
public class SendMessageRequest {
    @JsonProperty("chat_id")
    public long chatId;
    @JsonProperty("text")
    public String text;
    @JsonProperty("parse_mode")
    public ParseMode parseMode;
    @JsonProperty("replay_to_message_id")
    public Long replayToMessageId;
    @JsonProperty("allow_sending_without_reply")
    public Boolean allowSendingWithoutReply;
    @JsonProperty("reply_markup")
    public ReplyMarkup replyMarkup;
}
