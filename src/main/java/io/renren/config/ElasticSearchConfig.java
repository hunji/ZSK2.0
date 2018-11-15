package io.renren.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
/**
 * @author hunji
 * @date 2018/11/5
 */
@Configuration
public class ElasticSearchConfig {
    @Value("${elasticsearch.host}")
    private String esHost;

    @Value("${elasticsearch.port}")
    private int esPort;

    @Value("${elasticsearch.cluster.name}")
    private String esName;

    @PostConstruct
    void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

//    @Bean
//    public TransportClient esClient() throws UnknownHostException {
//        Settings settings = Settings.builder()
//                .put("cluster.name", this.esName)
//                .put("client.transport.sniff", true)
//                .build();
//
//        TransportAddress master = new TransportAddress(InetAddress.getByName(esHost), Integer.valueOf(esPort));
//
//        TransportClient client = new PreBuiltTransportClient(settings)
//                .addTransportAddress(master);
//        return client;
//
//    }

    /**
     * 9200是http rest的端口 使用RestHighLevelClient
     * 9300是tcp的端口 使用TransportClient
     * @return
     */
    @Bean
    public RestHighLevelClient esClient(){
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost(esHost, esPort, "http")));
        return client;
    }
}
