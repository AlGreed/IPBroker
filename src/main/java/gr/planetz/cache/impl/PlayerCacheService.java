package gr.planetz.cache.impl;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import gr.planetz.cache.CacheService;

public class PlayerCacheService implements CacheService {

    private static final Logger            LOG       = Logger.getLogger(PlayerCacheService.class);

    private final Cache<String, String>    cache;

    private final ScheduledExecutorService executor  = Executors.newScheduledThreadPool(1);

    private final AtomicBoolean            isRunning = new AtomicBoolean(false);

    private final long                     period;

    public PlayerCacheService(final long expirationTimeInMillis, final long cleanUpPeriod) {
        LOG.debug("PlayerCacheService initialized");
        Validate.isTrue(expirationTimeInMillis > 0, "|expirationTimeInMillis| must be greater than null!");
        Validate.isTrue(cleanUpPeriod > 0, "|cleanUpPeriod| must be greater than null!");
        this.period = cleanUpPeriod;
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(expirationTimeInMillis, TimeUnit.SECONDS).build();
        startCleaningUp();
    }

    private void startCleaningUp() {
        LOG.debug("Cache cleanup service is started.");
        if (!isRunning()) {
            this.isRunning.set(true);
            this.executor.scheduleAtFixedRate((Runnable) () -> {
                try {
                    LOG.debug("Cleaning up cache...");
                    cache.cleanUp();
                } catch (final Exception e) {
                    LOG.error("Cleaning up process cannot clean up cache!");
                }
            }, 0, this.period, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void putPlayer(final String nickname, final String ip) {
        LOG.info("PUT: nickname[" + nickname + "] ip[" + ip + "]");
        this.cache.put(nickname, ip);
    }

    @Override
    public Map<String, String> getPlayers() {
        return this.cache.asMap();
    }

    @Override
    public boolean containsPlayers(String nickname, String ip) {
        final String containedIp = getPlayers().get(nickname);
        return containedIp != null && containedIp.equals(ip);
    }

    @Override
    public boolean isRunning() {
        return this.isRunning.get();
    }

    @Override
    public long getSize() {
        return this.cache.size();
    }

    @Override
    public void close() throws Exception {
        this.isRunning.set(false);
        this.executor.shutdown();
        try {
            if (!this.executor.awaitTermination(10, TimeUnit.SECONDS)) {
                this.executor.shutdownNow();
                if (!this.executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    LOG.warn("Cleaning up process did not terminate!");
                }
            }
        } catch (final InterruptedException ie) {
            this.executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
