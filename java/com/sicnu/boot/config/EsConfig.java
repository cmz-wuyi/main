package com.sicnu.boot.config;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * description:  elasticsearch配置类
 *
 * @author :  胡建华
 * Data:    2023/01/05 21:26
 */
@Configuration
public class EsConfig {

    @Value("${elasticSearch.url}")
    public String esUrl;

    @Bean
    public RestHighLevelClient client() {
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(esUrl)));
    }
}
