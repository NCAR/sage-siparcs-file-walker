# sage-siparcs-file-walker

## Links

https://www.tutorialspoint.com/elasticsearch/

https://www.elastic.co/guide/en/elasticsearch/reference/current/security-basic-setup.html#encrypt-internode-communication

https://www.elastic.co/guide/en/elasticsearch/reference/current/optimistic-concurrency-control.html

https://www.devopsschool.com/blog/what-is-seq_no-and-primary_term-in-elasticsearch/

https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/java-rest-low-usage-logging.html

Turn off telemetry:
https://www.elastic.co/guide/en/kibana/current/telemetry-settings-kbn.html

Bulk Indexing
https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/indexing-bulk.html#_indexing_application_objects

Compare two dates to determine file modified dates:
https://stackoverflow.com/questions/70665811/how-to-compare-two-date-fields-in-same-document-in-elasticsearch

## Build Image
```
$ podman build -t sage-siparcs-file-walker .
```

# IDE Deployment

We require the usage of external spring configurations to override default values or unset values in our default application.yml file.

[Externalized Configuration](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/boot-features-external-config.html)

[Spring Boot and multiple external configuration files](https://stackoverflow.com/q/25855795/42962) - Stack Overflow

## Example Run Config

VM options:
```
-Djavax.net.ssl.trustStore=/Users/nhook/Java/home-file-walker/jssecacerts1
-Dspring.config.additional-location=file:/Users/nhook/Java/home-file-walker/application.yml
```

# Things That Have Been Done to (try and) Speed Up Elasticsearch

[Disable swapping](https://www.elastic.co/guide/en/elasticsearch/reference/current/setup-configuration-memory.html) - 
Swap was disabled on the host vm and therefore the swap has to be disabled on the podman container.

Increased the number of shards from 1 to 7.  7 was picked because of some rules we found that a shard can hold ~200 
million records and a shard should be sized between 4 and 20 gigs.  We are expecting ~646 million records.

Reduced the number of replicas to 0.

We futzed with the sizing of our Bulk Ingester's   maxOperations, flushInterval, and maxConcurrentRequests.  Changing
the maxConcurrentRequests seemed to help the most at one point, but seemed to reach a maximum improvement at 4.

Changed the number of items queried when filling the queues (media-type and scientific metadata).

# Future Considerations/Ideas

Consider building a workflow prior to initially inserting the data into elasticsearch.  You are thinking that updates are
generally slow and while calculating media-type and scientific metadata really seems to slowdown the insert process, it
might be quicker/better overall to just calculate everything we can from a file prior to inserting it into the index since
it seems as though updates are painfully slow with elasticsearch.
