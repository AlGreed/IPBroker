package gr.planetz.cache;

import java.util.Map;

public interface CacheService extends AutoCloseable {

    void putPlayer(final String nickname, final String ip);

    Map<String, String> getPlayers();

    boolean isRunning();

    long getSize();
}
