package edu.ncar.cisl.sage.logging;

import co.elastic.clients.elasticsearch._helpers.bulk.BulkListener;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BulkIngesterListenerLogger implements BulkListener {

    private static final Logger LOG = LoggerFactory.getLogger(BulkIngesterListenerLogger.class);

    @Override
    public void beforeBulk(long executionId, BulkRequest request, List list) {

        LOG.debug("Before bulk executionId: {} request: {} list: {}", executionId, request, list);
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, List list, BulkResponse response) {

        LOG.debug("After bulk executionId: {} request: {} list: {} response: {}", executionId, request, list, response);
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, List list, Throwable failure) {

        LOG.debug(String.format("After bulk executionId: {} request: {} list: {}", executionId, request, list), failure);
    }
}
