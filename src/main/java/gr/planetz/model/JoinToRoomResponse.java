package gr.planetz.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinToRoomResponse {

    @JsonProperty("Players")
    private final Map<String, String> players;

    @JsonCreator
    public JoinToRoomResponse(@JsonProperty("Players") final Map<String, String> players) {
        this.players = players;
    }

    public Map<String, String> getPlayers() {
        return this.players;
    }

    @Override
    public int hashCode() {
        return this.players != null ? this.players.hashCode() : 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JoinToRoomResponse)) {
            return false;
        }

        final JoinToRoomResponse that = (JoinToRoomResponse) o;

        return this.players != null ? this.players.equals(that.players) : that.players == null;

    }
}
