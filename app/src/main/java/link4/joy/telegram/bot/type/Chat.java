package link4.joy.telegram.bot.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Chat {
    @JsonProperty("id")
    public long id;
    @JsonProperty("type")
    public String type;
    @JsonProperty("username")
    public String username;
    @JsonProperty("first_name")
    public String firstName;
    @JsonProperty("last_name")
    public String lastName;

    public Chat() {
    }

    @JsonCreator
    public Chat(@JsonProperty("id") long id, @JsonProperty("type") String type,
                @JsonProperty("username") String username, @JsonProperty("first_name") String firstName,
                @JsonProperty("last_name") String lastName) {
        this.id = id;
        this.type = type;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
