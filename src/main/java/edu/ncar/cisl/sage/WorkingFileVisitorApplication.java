package edu.ncar.cisl.sage;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ncar.cisl.sage.filewalker.FileWalker;
import edu.ncar.cisl.sage.filewalker.LoggingFileVisitor;
import edu.ncar.cisl.sage.identification.IdStrategy;
import edu.ncar.cisl.sage.identification.Md5Calculator;
import edu.ncar.cisl.sage.metadata.MetadataStrategy;
import edu.ncar.cisl.sage.metadata.impl.MediaTypeMetadataStrategyImpl;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import edu.ncar.cisl.sage.repository.impl.EsFileRepositoryImpl;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.tika.Tika;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Configuration
@EnableScheduling
public class WorkingFileVisitorApplication {

    public static final String esIndex = "files";

    private ApplicationEventPublisher applicationEventPublisher;

    public static void main(String[] args) {
            SpringApplication.run(WorkingFileVisitorApplication.class, args);
    }

    @Bean
    public LoggingFileVisitor loggingFileVisitor(@Value("${config.ignoredPaths}") List<String> ignoredPaths) {

        return new LoggingFileVisitor(ignoredPaths);
    }

    @Bean
    public FileWalker fileWalker(@Value("${walker.startingPath}") String startingPath, LoggingFileVisitor visitor) {

        return new FileWalker(Path.of(startingPath), visitor, Clock.systemDefaultZone());
    }

    @Bean
    public ElasticsearchClient createClient(@Value("${xpack.security.enabled}") boolean isSecurityEnabled,
                                            @Value("${http.host}") String hostname,
                                            @Value("${http.port}") int port,
                                            @Value("${elastic.username}") String username,
                                            @Value("${elastic.password}") String password) {

        if (isSecurityEnabled) {

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

            RestClient restClient = RestClient.builder(new HttpHost(hostname, port, "HTTPS"))
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                    .build();

            JacksonJsonpMapper mapper = new JacksonJsonpMapper();
            mapper.objectMapper().registerModule(new JavaTimeModule());

            ElasticsearchTransport transport = new RestClientTransport(restClient, mapper);

            return new ElasticsearchClient(transport);

        } else {

            RestClient restClient = RestClient.builder(new HttpHost(hostname, port)).build();

            JacksonJsonpMapper mapper = new JacksonJsonpMapper();
            mapper.objectMapper().registerModule(new JavaTimeModule());

            ElasticsearchTransport transport = new RestClientTransport(restClient, mapper);

            return new ElasticsearchClient(transport);
        }
    }

    @Bean
    public Tika createTika() {

        return new Tika();
    }

    @Bean
    public IdStrategy createIdStrategy() {

        return new Md5Calculator();
    }

    @Bean
    public MetadataStrategy createMetadataStrategy(Tika tika) {

        return new MediaTypeMetadataStrategyImpl(tika);
    }

    @Bean
    public EsFileRepository createEsFileRepository(ElasticsearchClient esClient) {

        BulkIngester<Void> ingester = BulkIngester.of(b -> b
                .client(esClient)
                .maxOperations(10)
                .flushInterval(2, TimeUnit.SECONDS) //MINUTES
        );

        return new EsFileRepositoryImpl(esClient, ingester);
    }
}