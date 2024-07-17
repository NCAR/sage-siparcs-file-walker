package edu.ncar.cisl.sage.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.indices.GetFieldMappingResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import static co.elastic.clients.elasticsearch.ingest.Processor.Kind.Set;
import static edu.ncar.cisl.sage.WorkingFileVisitorApplication.esDirStateIndex;
import static edu.ncar.cisl.sage.WorkingFileVisitorApplication.esIndex;

@Component
public class CheckIndex {

    private final ElasticsearchClient esClient;

    @Autowired
    public CheckIndex(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    @Value("${esIndex.numberOfShards}")
    private String numberOfShards;

    @Value("${esIndex.numberOfReplicas}")
    private String numberOfReplicas;

    @EventListener
    public void checkIndex(ApplicationStartedEvent event) throws IOException {

        BooleanResponse existsResponse = esClient.indices().exists(b -> b.index(esIndex));

        Property date = Property.of(p -> p.date(d -> d.format("basic_date_time")));
        Property object = Property.of(p -> p.object(o -> o));

        if (!existsResponse.value()) {

            // We use explicit mapping of our indexes:
            // https://www.elastic.co/guide/en/elasticsearch/reference/current/explicit-mapping.html

            // It is easy to mess this mapping up.  Please read about the different data types in ElasticSearch
            // before making any changes to these values:
            // https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html
            //
            // An explanation of the differences of text, keyword, and wildcard:
            // https://www.elastic.co/blog/find-strings-within-strings-faster-with-the-new-elasticsearch-wildcard-field

            // Explanation of fields:
            // https://www.elastic.co/guide/en/elasticsearch/reference/current/multi-fields.html

            Property boolean_ = Property.of(p -> p.boolean_(b -> b));
            Property text = Property.of(p -> p.text(t -> t));
            Property long_ = Property.of(p -> p.long_(l -> l));
            Property keyword = Property.of(p -> p.keyword(k -> k));
            Property wildcard = Property.of(p -> p.wildcard(w -> w));

            esClient.indices().create(c -> c
                    .index(esIndex)
                    .mappings(m -> m
                            .properties("dateCreated", date)
                            .properties("dateLastIndexed", date)
                            .properties("dateModified", date)
                            .properties("directory", boolean_)
                            .properties("error", boolean_)
                            .properties("errorMessage", keyword)
                            .properties("missing", boolean_)
                            .properties("dateMissing", date)
                            .properties("extension", keyword)
                            .properties("mediaType", keyword)
                            .properties("dateMediaTypeUpdated", date)
                            .properties("fileIdentifier", wildcard)
                            .properties("fileName", text)
                            .properties("owner", text)
                            .properties("path", keyword)
                            .properties("size", long_)
                    )
                    .settings(s -> s
                            .numberOfShards(numberOfShards)
                            .numberOfReplicas(numberOfReplicas)
                    )
            );
        }

        if(!fieldMappingExists("scientificMetadata")) {

            esClient.indices().putMapping(m -> m.index(esIndex).properties("scientificMetadata",object));
            esClient.indices().putMapping(m -> m.index(esIndex).properties("dateScientificMetadataUpdated", date));
        }
    }

    private boolean fieldMappingExists(String field) throws IOException {

        GetFieldMappingResponse response = esClient.indices().getFieldMapping(m -> m.index(esIndex).fields(field));
        return !response.result().get(esIndex).mappings().isEmpty();
    }

    @EventListener
    public void checkDirStateIndex(ApplicationStartedEvent event) throws IOException {

        BooleanResponse existsResponse = esClient.indices().exists(b -> b.index(esDirStateIndex));

        if (!existsResponse.value()) {

            Map<String, Property> fields = Collections.singletonMap("keyword", Property.of(p -> p.keyword(k -> k.ignoreAbove(256))));
            Property date = Property.of(p -> p.date(d -> d.format("basic_date_time")));
            Property text = Property.of(p -> p.text(t -> t.fields(fields)));

            esClient.indices().create(c -> c
                    .index(esDirStateIndex)
                    .mappings(m -> m
                            .properties("id", text)
                            .properties("completed", text)
                            .properties("dateCreated", date)
                            .properties("dateUpdated", date)
                    )
            );
        }
    }
}
