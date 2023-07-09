package com.mishlen.elastic_search.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RequestDTO {
    private String json;
    public Data data;

    public class Data {
        public Map<String, String> data;
    }
}
