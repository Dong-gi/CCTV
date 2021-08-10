package link4.joy.telegram.bot.res;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import link4.joy.telegram.bot.type.Update;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUpdatesResponse extends BaseResponse {
    @JsonProperty("result")
    public Update[] result;

    public GetUpdatesResponse() {
    }

    @JsonCreator
    public GetUpdatesResponse(@JsonProperty("result") Update[] result) {
        this.result = result;
    }
}
