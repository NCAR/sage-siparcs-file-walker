package edu.ncar.cisl.sage;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ncar.cisl.sage.filewalker.FileWalker;
import edu.ncar.cisl.sage.filewalker.LoggingFileVisitor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Configuration
public class WorkingFileVisitorApplication  {

    @Value("${walker.startingPath}")
    private String startingPath;

//	Create Interface for FileVisitor so that multiple can be made and passed in to be used??
//	Needs to be updated so that it supports FileVisitor<Path> throughout the program ??
//	@Value("${walker.fileVisitor})
//	private FileVisitor<Path> fileVisitor

    @Value("${config.ignoredPaths}")
    private List<String> ignoredPaths;

    public static void main(String[] args) {
        SpringApplication.run(WorkingFileVisitorApplication.class, args);
    }

    @Bean
    public LoggingFileVisitor loggingFileVisitor() {

        return new LoggingFileVisitor(ignoredPaths);
    }
    @Bean
    public FileWalker fileWalker(LoggingFileVisitor visitor) {

        return new FileWalker(Path.of(startingPath), visitor, Clock.systemDefaultZone());
    }

    @Bean
    public ElasticsearchClient createClient() {

        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();

        JacksonJsonpMapper mapper = new JacksonJsonpMapper();
        mapper.objectMapper().registerModule(new JavaTimeModule());

        ElasticsearchTransport transport = new RestClientTransport(restClient, mapper);

        return(new ElasticsearchClient(transport));
    }

    @Bean
    public BulkIngester<Void> createBulkIngester(ElasticsearchClient esClient) {

        BulkIngester<Void> ingester = BulkIngester.of(b -> b
                .client(esClient)
                .maxOperations(1000)
                .flushInterval(1, TimeUnit.MINUTES)
        );

        return ingester;
    }

}