package edu.ncar.cisl.sage.metadata;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.EvictionConfig;
import org.apache.commons.pool2.impl.EvictionPolicy;

public class ExpirationEvictionPolicy<Tika> implements EvictionPolicy<Tika> {

    @Override
    public boolean evict(EvictionConfig evictionConfig, PooledObject<Tika> tika, int i) {

        long creationTime = tika.getFullDuration().toSeconds();
        return creationTime > 60;
    }
}
