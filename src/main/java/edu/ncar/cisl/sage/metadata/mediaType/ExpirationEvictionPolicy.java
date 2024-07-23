package edu.ncar.cisl.sage.metadata.mediaType;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.EvictionConfig;
import org.apache.commons.pool2.impl.EvictionPolicy;

public class ExpirationEvictionPolicy<Tika> implements EvictionPolicy<Tika> {

    @Override
    public boolean evict(EvictionConfig evictionConfig, PooledObject<Tika> tika, int i) {

        return tika.getFullDuration().toSeconds() > 60;
    }
}
