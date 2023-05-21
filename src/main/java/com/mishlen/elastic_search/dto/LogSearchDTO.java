package com.mishlen.elastic_search.dto;

import lombok.Data;

@Data
public class LogSearchDTO {
    private String application;
    private String level;
    private String env;
    private Long beginDate;
    private Long endDate;
}
