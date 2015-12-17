package gr.planetz.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinToRoomRequest {

    @JsonProperty("nickname")
    private final String nickname;

    public JoinToRoomRequest(@JsonProperty("nickname") final String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }

    @Override
    public int hashCode() {
        return this.nickname != null ? this.nickname.hashCode() : 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JoinToRoomRequest)) {
            return false;
        }

        final JoinToRoomRequest that = (JoinToRoomRequest) o;

        return this.nickname != null ? this.nickname.equals(that.nickname) : that.nickname == null;

    }
}
