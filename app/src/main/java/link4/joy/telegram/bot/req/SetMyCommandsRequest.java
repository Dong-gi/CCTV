package link4.joy.telegram.bot.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import link4.joy.telegram.bot.type.BotCommand;

@JsonInclude(Include.NON_NULL)
public class SetMyCommandsRequest {
    @JsonProperty("commands")
    public List<BotCommand> commands = new ArrayList<>();
}
