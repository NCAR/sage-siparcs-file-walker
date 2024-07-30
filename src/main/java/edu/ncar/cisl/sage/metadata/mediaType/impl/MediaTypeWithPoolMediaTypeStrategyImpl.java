package edu.ncar.cisl.sage.metadata.mediaType.impl;

import edu.ncar.cisl.sage.metadata.mediaType.MediaTypeStrategy;
import org.apache.commons.pool2.ObjectPool;
import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class MediaTypeWithPoolMediaTypeStrategyImpl implements MediaTypeStrategy {

    private final ObjectPool<Tika> pool;

    private static final Logger LOG = LoggerFactory.getLogger(MediaTypeWithPoolMediaTypeStrategyImpl.class);

    public MediaTypeWithPoolMediaTypeStrategyImpl(ObjectPool<Tika> pool) {

        this.pool = pool;
    }

    @Override
    public String calculateMetadata(Path path) {

        String value = MediaType.OCTET_STREAM.toString();

        Tika pooledObject = null;

        try {

            pooledObject = this.pool.borrowObject();

            try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(path))) {

                if (Files.notExists(path)) {

                    LOG.debug("Does not exist: {}", path);
                }
                value = pooledObject.detect(inputStream, path.getFileName().toString());

            } catch (NoSuchFileException e) {

                LOG.error(e.getMessage(), e);

                throw e;

            } catch (IOException e) {

                LOG.error(e.getMessage(), e, path);
            }

        } catch (Exception e) {

            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);

        } finally {

            if (pooledObject != null) {

                try {

                    pool.returnObject(pooledObject);

                } catch (Exception ignored) {

                }
            }
        }

        return value;
    }
}
