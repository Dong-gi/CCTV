package link4.joy.telegram.bot.res;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse {
    public static final BaseResponse NO_HANDLER = new BaseResponse(true, "NO_HANDLER");
    public static final BaseResponse NOTHING_TO_DO = new BaseResponse(true, "NOTHING TO DO");

    @JsonProperty("ok")
    public boolean ok;
    @JsonProperty("description")
    public String description;

    public BaseResponse() {
    }

    @JsonCreator
    public BaseResponse(@JsonProperty("ok") boolean ok, @JsonProperty("description") String description) {
        this.ok = ok;
        this.description = description;
    }

    public boolean isNoHandler() {
        return this == NO_HANDLER;
    }

    public boolean isNothingToDo() {
        return this == NOTHING_TO_DO;
    }
}
