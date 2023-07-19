package com.mishlen.elastic_search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Поисковая строка с параметрами")
public class RequestDTO {
    @Schema(description = "json", example = "{\"application\": \"qw_auth\",\n" +
            " \"level\": \"error\",\n" +
            " \"env\": \"prod\",\n" +
            " \"beginDate\": \"1683111757304\",\n" +
            " \"endDate\": \"1683111777304\"\n" +
            "}")
    private String json;
    @Schema(description = "разбивка произвольных параметров из json, полученного от пользователя")
    public Data data;

    public class Data {
        public Map<String, String> data;
    }
}
