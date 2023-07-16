package com.mishlen.elastic_search.dto;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;

@Data
@Hidden
public class LogRequestDTO {
    private String application;
    private String level;
    private String env;
    private String value;
    private Long date;
}
