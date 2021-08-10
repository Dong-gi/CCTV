package link4.joy.telegram.bot.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ReplyKeyboardMarkup implements ReplyMarkup {
    @JsonProperty("keyboard")
    public KeyboardButton[][] keyboard;
    @JsonProperty("resize_keyboard")
    public Boolean resizeKeyboard = Boolean.TRUE;
    @JsonProperty("one_time_keyboard")
    public Boolean oneTimeKeyboard = Boolean.TRUE;
    @JsonProperty("input_field_placeholder")
    public String inputFieldPlaceholder;
    @JsonProperty("selective")
    public Boolean selective;

    public ReplyKeyboardMarkup() {
    }

    @JsonCreator
    ReplyKeyboardMarkup(@JsonProperty("keyboard") KeyboardButton[][] keyboard,
                        @JsonProperty("resize_keyboard") Boolean resizeKeyboard,
                        @JsonProperty("one_time_keyboard") Boolean oneTimeKeyboard,
                        @JsonProperty("input_field_placeholder") String inputFieldPlaceholder,
                        @JsonProperty("selective") Boolean selective) {
        this.keyboard = keyboard;
        this.resizeKeyboard = resizeKeyboard;
        this.oneTimeKeyboard = oneTimeKeyboard;
        this.inputFieldPlaceholder = inputFieldPlaceholder;
        this.selective = selective;
    }
}
