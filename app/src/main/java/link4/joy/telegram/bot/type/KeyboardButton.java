package link4.joy.telegram.bot.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class KeyboardButton implements ReplyMarkup {
    @JsonProperty("text")
    public String text;

    public KeyboardButton() {
    }

    @JsonCreator
    public KeyboardButton(@JsonProperty("text") String text) {
        this.text = text;
    }
}
