package com.mishlen.elastic_search.dto;

import lombok.Data;

@Data
public class SearchResponseFileDTO extends SearchResponseDTO{
    private String application;
    private String level;
    private String env;
    private String value;
    private String date;
}
