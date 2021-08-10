package link4.joy.telegram.bot.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Message {
    @JsonProperty("message_id")
    public long messageId;
    @JsonProperty("from")
    public User from;
    @JsonProperty("date")
    public long date;
    @JsonProperty("chat")
    public Chat chat;
    @JsonProperty("text")
    public String text;

    public Message() {
    }

    @JsonCreator
    public Message(@JsonProperty("message_id") long messageId, @JsonProperty("from") User from,
                   @JsonProperty("date") long date, @JsonProperty("chat") Chat chat, @JsonProperty("text") String text) {
        this.messageId = messageId;
        this.from = from;
        this.date = date;
        this.chat = chat;
        this.text = text;
    }
}
