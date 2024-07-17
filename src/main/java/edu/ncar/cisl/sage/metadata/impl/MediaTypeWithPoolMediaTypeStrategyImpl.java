package edu.ncar.cisl.sage.metadata.impl;

import edu.ncar.cisl.sage.metadata.MediaTypeStrategy;
import org.apache.commons.pool2.ObjectPool;
import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class MediaTypeWithPoolMediaTypeStrategyImpl implements MediaTypeStrategy {

    private final ObjectPool<Tika> pool;

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

                    System.out.println("Does not exist: " + path);
                }
                value = pooledObject.detect(inputStream, path.getFileName().toString());

            } catch (NoSuchFileException e) {

                System.out.println("Exception: " + e);
                e.printStackTrace();

                throw e;

            } catch (IOException e) {

                System.out.println("Exception: " + e);
                e.printStackTrace();
            }

        } catch (Exception e) {

            System.out.println("Exception: " + e);
            e.printStackTrace();
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
