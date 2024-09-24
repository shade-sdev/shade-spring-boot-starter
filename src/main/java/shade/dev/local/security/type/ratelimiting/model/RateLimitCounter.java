package shade.dev.local.security.type.ratelimiting.model;

import java.util.concurrent.TimeUnit;

public class RateLimitCounter {

    private final int maxRequests;
    private final long resetTimeMillis;

    private long lastResetTime;
    private int count;

    public RateLimitCounter(int maxRequests, int time, TimeUnit timeUnit)
    {
        this.maxRequests = maxRequests;
        this.resetTimeMillis = timeUnit.toMillis(time);
        this.lastResetTime = System.currentTimeMillis();
        this.count = 0;
    }

    public void incrementCount()
    {
        resetIfNeeded();
        count++;
    }

    public boolean isOverLimit()
    {
        resetIfNeeded();
        return count >= maxRequests;
    }

    private void resetIfNeeded()
    {
        long now = System.currentTimeMillis();
        if (now - lastResetTime >= resetTimeMillis) {
            count = 0;
            lastResetTime = now;
        }
    }

}
