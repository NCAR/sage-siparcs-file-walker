package edu.ncar.cisl.sage.metadata.mediaType;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectState;
import org.apache.commons.pool2.impl.EvictionConfig;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.time.Duration;
import java.util.Deque;

public class ExpirationEvictionPolicyTest {

    private ExpirationEvictionPolicy<Tika> policy;

    private static final EvictionConfig NULL_EVICTION_CONFIG = null;
    private static final int IDLE_COUNT_NOT_USED = 0;

    @BeforeEach
    public void setup() {

        this.policy = new ExpirationEvictionPolicy<>();
    }

    @Test
    public void give_duration_60_or_less__when_evict__then_return_false() {

        Assertions.assertFalse(callEvict(0));
        Assertions.assertFalse(callEvict(60));
    }

    @Test
    public void given_duration_more_than_60__when_evict__then_return_true() {

        Assertions.assertTrue(callEvict(61));
    }

    private boolean callEvict(int duration) {

        return policy.evict(NULL_EVICTION_CONFIG, new PooledObjectStub(duration), IDLE_COUNT_NOT_USED);
    }

    public class PooledObjectStub implements PooledObject<Tika> {

        int duration;

        public PooledObjectStub(int duration) {

            this.duration = duration;
        }

        @Override
        public Duration getFullDuration() {

            return Duration.ofSeconds(duration);
        }

        @Override
        public boolean allocate() {
            return false;
        }

        @Override
        public int compareTo(PooledObject<Tika> pooledObject) {
            return 0;
        }

        @Override
        public boolean deallocate() {
            return false;
        }

        @Override
        public boolean endEvictionTest(Deque<PooledObject<Tika>> deque) {
            return false;
        }

        @Override
        public long getActiveTimeMillis() {
            return 0;
        }

        @Override
        public long getCreateTime() {
            return 0;
        }

        @Override
        public long getIdleTimeMillis() {
            return 0;
        }

        @Override
        public long getLastBorrowTime() {
            return 0;
        }

        @Override
        public long getLastReturnTime() {
            return 0;
        }

        @Override
        public long getLastUsedTime() {
            return 0;
        }

        @Override
        public Tika getObject() {
            return null;
        }

        @Override
        public PooledObjectState getState() {
            return null;
        }

        @Override
        public void invalidate() {

        }

        @Override
        public void markAbandoned() {

        }

        @Override
        public void markReturning() {

        }

        @Override
        public void printStackTrace(PrintWriter printWriter) {

        }

        @Override
        public void setLogAbandoned(boolean b) {

        }

        @Override
        public boolean startEvictionTest() {
            return false;
        }

        @Override
        public void use() {

        }
    }
}
