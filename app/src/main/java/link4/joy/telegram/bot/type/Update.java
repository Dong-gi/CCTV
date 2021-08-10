package link4.joy.telegram.bot.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Update {
    @JsonProperty("update_id")
    public long updateId;
    @JsonProperty("message")
    public Message message;

    public Update() {
    }

    @JsonCreator
    public Update(@JsonProperty("update_id") long updateId, @JsonProperty("message") Message message) {
        this.updateId = updateId;
        this.message = message;
    }
}
