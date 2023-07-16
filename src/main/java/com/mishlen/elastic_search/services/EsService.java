package com.mishlen.elastic_search.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishlen.elastic_search.dto.*;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.zip.GZIPOutputStream;

@Service
public class EsService {

    @Data
    public static class Log {
        private String application;
        private String level;
        private String env;
        private String value;
        private String date;
    }

    private final static String INDEX_NAME = "log";

    private final RestHighLevelClient esClient;

    public EsService(RestHighLevelClient esClient) { this.esClient = esClient; }

    public String updateLog(LogRequestDTO requestObject) throws IOException {
        Log log = new Log();
        log.setApplication(requestObject.getApplication());
        log.setEnv(requestObject.getEnv());
        log.setLevel(requestObject.getLevel());
        log.setValue(requestObject.getValue());
        log.setDate(Instant.ofEpochMilli(requestObject.getDate()).toString());

        String id = UUID.randomUUID().toString();
        IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
        indexRequest.id(id);
        ObjectMapper mapper = new ObjectMapper();
        indexRequest.source(mapper.writeValueAsString(log), XContentType.JSON);
        esClient.index(indexRequest, RequestOptions.DEFAULT);

        return id;
    }

    public List<SearchResponseDTO> search(RequestDTO logSearchDTO, boolean zip) throws Exception {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();

        try {
            map = mapper.readValue(logSearchDTO.getJson(), Map.class);
            System.out.println(map);
        }  catch (IOException ex) {
            ex.printStackTrace();
        }

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        // If the period is not filled, we send data for the last 5 minutes
        if (map.get("beginDate") == null) {
            map.put("beginDate", String.valueOf(Instant.ofEpochMilli(System.currentTimeMillis()).toEpochMilli() - 18000000));
        }
        if (map.get("endDate") == null) {
            map.put("endDate", String.valueOf(Instant.ofEpochMilli(System.currentTimeMillis()).toEpochMilli()));
        }
        query.must(QueryBuilders.rangeQuery("date").gte(map.get("beginDate")).lte(map.get("endDate")).format("epoch_millis"));

        for(Map.Entry entry: map.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();

            if (key != "beginDate" && key != "endDate") {
                query.filter(QueryBuilders.termQuery(key, value));
            }
        }

        searchSourceBuilder.query(query);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        List<SearchResponseDTO> listResponse = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits().getHits()) {
            //indexId = hit.getId();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            SearchResponseFileDTO responseFile = new SearchResponseFileDTO();
            responseFile.setApplication((String) sourceAsMap.get("application"));
            responseFile.setLevel((String) sourceAsMap.get("level"));
            responseFile.setEnv((String) sourceAsMap.get("env"));
            responseFile.setValue((String) sourceAsMap.get("value"));
            responseFile.setDate((String) sourceAsMap.get("date"));
            listResponse.add(responseFile);
        }

        if (zip) {
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String jsonLogs = objectWriter.writeValueAsString(listResponse);
            List<SearchResponseDTO> link = new ArrayList<>();
            link.add(new SearchResponseLinkDTO(pushToCloud(jsonLogs)));
            return link;
        } else {
            return listResponse;
        }
    }

    private String pushToCloud(String jsonLogs) throws IOException {
        String name = "logs.json";
        FileOutputStream fos = new FileOutputStream(name);
        GZIPOutputStream gz = new GZIPOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(gz);
        oos.writeObject(jsonLogs);
        oos.close();
        // TODO: тут архив надо куда то загрузить, и отдавать ссылку
        return name;
    }
}
