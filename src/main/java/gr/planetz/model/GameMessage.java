package gr.planetz.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by olno on 11/01/16.
 */
public class GameMessage {

    @JsonProperty(value = "nickname", required = true)
    private final String nickname;

    @JsonProperty(value = "message", required = true)
    private final String message;

    public GameMessage(@JsonProperty("nickname") final String nickname, @JsonProperty("message") String message) {
        this.nickname = nickname;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public String getNickname() {
        return this.nickname;
    }
}
