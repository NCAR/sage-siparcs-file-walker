package edu.ncar.cisl.sage.main.config;

import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.tika.Tika;

public class TikaPooledObjectFactory implements PooledObjectFactory<Tika> {

    @Override
    public void activateObject(PooledObject pooledObject) {}

    @Override
    public void destroyObject(PooledObject pooledObject) {}

    @Override
    public void destroyObject(PooledObject p, DestroyMode destroyMode) throws Exception {

        PooledObjectFactory.super.destroyObject(p, destroyMode);
    }

    @Override
    public PooledObject makeObject() {

        return new DefaultPooledObject<>(new Tika());
    }

    @Override
    public void passivateObject(PooledObject pooledObject) {}

    @Override
    public boolean validateObject(PooledObject pooledObject) { return false; }
}
