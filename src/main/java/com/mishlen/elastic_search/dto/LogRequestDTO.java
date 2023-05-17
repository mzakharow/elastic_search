package com.mishlen.elastic_search.dto;

import lombok.Data;

@Data
public class LogRequestDTO {
    private String application;
    private String level;
    private String env;
    private String value;
    private Long gte;
    private Long lte;
}
