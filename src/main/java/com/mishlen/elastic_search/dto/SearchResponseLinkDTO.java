package com.mishlen.elastic_search.dto;

import lombok.Data;

@Data
public class SearchResponseLinkDTO extends SearchResponseDTO{
    String link;

    public SearchResponseLinkDTO(String link) {
        this.link = link;
    }
}
