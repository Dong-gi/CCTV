package link4.joy.telegram.bot.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class User {
    @JsonProperty("id")
    public long id;
    @JsonProperty("is_bot")
    public boolean isBot;
    @JsonProperty("first_name")
    public String firstName;
    @JsonProperty("last_name")
    public String lastName;
    @JsonProperty("username")
    public String username;
    @JsonProperty("language_code")
    public String languageCode;

    public User() {
    }

    @JsonCreator
    public User(@JsonProperty("id") long id, @JsonProperty("is_bot") boolean isBot,
                @JsonProperty("first_name") String firstName, @JsonProperty("last_name") String lastName,
                @JsonProperty("username") String username, @JsonProperty("language_code") String languageCode) {
        this.id = id;
        this.isBot = isBot;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.languageCode = languageCode;
    }
}
