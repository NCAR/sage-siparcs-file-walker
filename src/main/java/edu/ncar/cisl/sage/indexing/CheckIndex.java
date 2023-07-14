package edu.ncar.cisl.sage.indexing;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
public class CheckIndex {

    private final ElasticsearchClient esClient;

    @Autowired
    public CheckIndex(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    @EventListener
    public void checkIndex(ApplicationStartedEvent event) throws IOException {

        BooleanResponse existsResponse = esClient.indices().exists(b -> b.index("files"));

        if (!existsResponse.value()) {

            Map<String, Property> fields = Collections.singletonMap("keyword", Property.of(p -> p.keyword(k -> k.ignoreAbove(256))));
            Property date = Property.of(p -> p.date(d -> d.format("basic_date_time")));
            Property boolean_ = Property.of(p -> p.boolean_(b -> b.fields(fields)));
            Property text = Property.of(p -> p.text(t -> t.fields(fields)));
            Property long_ = Property.of(p -> p.long_(l -> l.fields(fields)));

            esClient.indices().create(c -> c
                    .index("files")
                    .mappings(m -> m
                            .properties("dateCreated", date)
                            .properties("dateLastIndexed", date)
                            .properties("dateModified", date)
                            .properties("directory", boolean_)
                            .properties("error", boolean_)
                            .properties("errorMessage", text)
                            .properties("extension", text)
                            .properties("fileIdentifier", text)
                            .properties("fileName", text)
                            .properties("owner", text)
                            .properties("path", text)
                            .properties("size", long_)
                    )
            );
        }
    }
}
