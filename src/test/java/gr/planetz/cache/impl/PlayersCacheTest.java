package gr.planetz.cache.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PlayersCacheTest {

    private static final long EXPIRATION_TIME = 1;

    private static final long CLEAN_UP_PERIOD = 500;

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNegativeExprationTime() {
        new PlayerCacheService(-2, CLEAN_UP_PERIOD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void costructorRejectsNeagtiveCleanUpPeriod() {
        new PlayerCacheService(EXPIRATION_TIME, -1);
    }

    @Test
    public void assertThatCleaningUpProcessIsStarted() throws Exception {
        final PlayerCacheService connectionPool = new PlayerCacheService(EXPIRATION_TIME, CLEAN_UP_PERIOD);
        assertTrue("Cleaning up process should run!", connectionPool.isRunning());
        connectionPool.close();
    }

    @Test
    public void assertThatCLeaningUpProcessIsStoppedAfterDestroy() throws Exception {
        final PlayerCacheService connectionPool = new PlayerCacheService(EXPIRATION_TIME, CLEAN_UP_PERIOD);
        connectionPool.close();
        assertFalse("Cleaning up process should be stopped!", connectionPool.isRunning());
    }

    @Test
    public void assertThatConnectionPoolIsWorkingCorrectly() throws Exception {
        final PlayerCacheService connectionPool = new PlayerCacheService(EXPIRATION_TIME, CLEAN_UP_PERIOD);
        connectionPool.putPlayer("A", "ip1");
        assertTrue("Wrong size of connection pool!", connectionPool.getSize() == 1);

        connectionPool.putPlayer("B", "ip2");
        assertTrue("Wrong size of connection pool!", connectionPool.getSize() == 2);

        Thread.sleep(EXPIRATION_TIME * 1000 + CLEAN_UP_PERIOD);
        assertTrue("Wrong size of connection pool:" + connectionPool.getSize(), connectionPool.getSize() == 0);
        connectionPool.close();
    }

    @Test
    public void assertThatConnectionRefreshesItsExpirationTimeAfterReading() throws Exception {
        final long sleepTimeLessThanExpirationTime = 400L;

        final PlayerCacheService connectionPool = new PlayerCacheService(EXPIRATION_TIME, CLEAN_UP_PERIOD);
        connectionPool.putPlayer("A", "ip1");
        assertTrue("Wrong size of connection pool!", connectionPool.getSize() == 1);

        Thread.sleep(sleepTimeLessThanExpirationTime);
        connectionPool.putPlayer("A", "ip1");
        assertTrue("Wrong size of connection pool!", connectionPool.getSize() == 1);

        Thread.sleep(sleepTimeLessThanExpirationTime);
        connectionPool.putPlayer("A", "ip1");
        assertTrue("Wrong size of connection pool!", connectionPool.getSize() == 1);

        Thread.sleep(sleepTimeLessThanExpirationTime);
        connectionPool.putPlayer("A", "ip1");
        assertTrue("Wrong size of connection pool!", connectionPool.getSize() == 1);

        Thread.sleep(sleepTimeLessThanExpirationTime);
        connectionPool.putPlayer("A", "ip1");
        assertTrue("Wrong size of connection pool!", connectionPool.getSize() == 1);
        connectionPool.close();
    }

}
