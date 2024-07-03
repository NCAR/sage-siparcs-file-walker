package edu.ncar.cisl.sage;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ncar.cisl.sage.identification.IdStrategy;
import edu.ncar.cisl.sage.identification.Md5Calculator;
import edu.ncar.cisl.sage.metadata.MediaTypeService;
import edu.ncar.cisl.sage.metadata.MetadataStrategy;
import edu.ncar.cisl.sage.metadata.TikaPooledObjectFactory;
import edu.ncar.cisl.sage.metadata.impl.MediaTypeWithPoolMetadataStrategyImpl;
import edu.ncar.cisl.sage.repository.EsDirectoryStateRepository;
import edu.ncar.cisl.sage.repository.EsFileRepository;
import edu.ncar.cisl.sage.repository.impl.EsDirectoryStateRepositoryImpl;
import edu.ncar.cisl.sage.repository.impl.EsFileRepositoryImpl;
import edu.ncar.cisl.sage.metadata.WorkflowMonitor;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Configuration
@EnableScheduling
public class WorkingFileVisitorApplication{

    public static final String esIndex = "file-walker-files";
    public static final String esDirStateIndex = "file-walker-dir-state";

    public static void main(String[] args) {
            SpringApplication.run(WorkingFileVisitorApplication.class, args);
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
    public QueueChannel mediaTypeChannel() {

        return new QueueChannel();
    }

    @Bean
    public WorkflowMonitor createWorkflowMonitor(QueueChannel mediaTypeChannel, @Value("${mediaTypeWorkflow.enabled}") boolean enabled) {

        return new WorkflowMonitor(mediaTypeChannel, enabled);
    }

    @Bean
    public BulkIngester<Void> createBulkIngester(ElasticsearchClient esClient,
                                                 @Value("${bulkIngester.maxOperations}") int maxOperations,
                                                 @Value("${bulkIngester.flushInterval}") int flushInterval,
                                                 @Value("${bulkIngester.maxConcurrentRequests}") int maxConcurrentRequests) {

        return BulkIngester.of(b -> b
                .client(esClient)
                .maxOperations(maxOperations)
                .flushInterval(flushInterval, TimeUnit.SECONDS)
                .maxConcurrentRequests(maxConcurrentRequests)
        );
    }

    @Bean
    public MediaTypeService createMediaTypeService(EsFileRepository esFileRepository, MetadataStrategy metadataStrategy) {

        return new MediaTypeService(esFileRepository, metadataStrategy);
    }

    @Bean
    public IdStrategy createIdStrategy() {

        return new Md5Calculator();
    }

    @Bean
    public ObjectPool<Tika> createPool(@Value("${mediaTypeWorkflow.tikaPoolSize}") int poolSize) {

        GenericObjectPoolConfig<Tika> config = new GenericObjectPoolConfig<>();
        config.setJmxEnabled(false);

        GenericObjectPool<Tika> pool = new GenericObjectPool<>(new TikaPooledObjectFactory(),config);
        pool.setMaxTotal(poolSize);
        return pool;
    }

    @Bean
    public MetadataStrategy createMediaTypeStrategy(ObjectPool<Tika> pool) {

        return new MediaTypeWithPoolMetadataStrategyImpl(pool);
    }

    @Bean
    public EsFileRepository createEsFileRepository(ElasticsearchClient esClient,
                                                   BulkIngester<Void> ingester,
                                                   @Value("${mediaTypeWorkflow.esQuerySize}") int esQuerySize) {

        return new EsFileRepositoryImpl(esClient, ingester, esQuerySize);
    }

    @Bean
    public EsDirectoryStateRepository createEsDirStateRepository(ElasticsearchClient esClient, BulkIngester<Void> ingester) {

        return new EsDirectoryStateRepositoryImpl(esClient, ingester);
    }
}