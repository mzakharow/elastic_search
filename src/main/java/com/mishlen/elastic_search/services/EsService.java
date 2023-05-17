package com.mishlen.elastic_search.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishlen.elastic_search.dto.LogRequestDTO;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EsService {


    @Data
    public static class Log {
        private String application;
        private String level;
        private String env;
        private String value;
        private String gte;
        private String lte;
    }

    private final static String INDEX_NAME = "log";

    private final ObjectMapper mapper = new ObjectMapper();

    private final RestHighLevelClient esClient;

    public EsService(RestHighLevelClient esClient) { this.esClient = esClient; }

    public void updateLog(String id, LogRequestDTO requestObject) throws IOException {
        Log log = new Log();
        log.setApplication(requestObject.getApplication());
        log.setEnv(requestObject.getEnv());
        log.setLevel(requestObject.getLevel());
        log.setValue(requestObject.getValue());
        log.setGte(Instant.ofEpochMilli(requestObject.getGte()).toString());
        log.setLte(Instant.ofEpochMilli(requestObject.getLte()).toString());
//        log.setGte(LocalDateTime.ofInstant(Instant.ofEpochMilli(requestObject.getGte()), ZoneId.systemDefault()));
//        log.setLte(LocalDateTime.ofInstant(Instant.ofEpochMilli(requestObject.getLte()), ZoneId.systemDefault()));

        IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
        indexRequest.id(id);
        indexRequest.source(mapper.writeValueAsString(log), XContentType.JSON);

        esClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    public List<Log> search(String searchString) throws Exception {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
////        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("text", searchString));
//
//        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(searchString, "title", "text"))
//                .query(QueryBuilders.matchPhraseQuery("title", searchString));
//
//        searchSourceBuilder.postFilter(QueryBuilders.multiMatchQuery(searchString, "title", "text"))
//                .query(QueryBuilders.matchPhraseQuery("title", searchString));

        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(searchString, "application"));

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Log> logs = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            //indexId = hit.getId();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            Log log = new Log();
            log.setApplication((String) sourceAsMap.get("application"));
            log.setLevel((String) sourceAsMap.get("level"));
            log.setEnv((String) sourceAsMap.get("env"));
            log.setValue((String) sourceAsMap.get("value"));
            log.setGte((String) sourceAsMap.get("gte"));
            log.setLte((String) sourceAsMap.get("lte"));
            logs.add(log);
        }
        return logs;
    }
}
