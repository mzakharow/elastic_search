package com.mishlen.elastic_search.dto;

import lombok.Data;

@Data
public class LogResponseDTO {
    private String id;
    public LogResponseDTO(String id) {
        this.id = id;
    }
}
