package com.mishlen.elastic_search.controllers;

import com.mishlen.elastic_search.dto.LogRequestDTO;
import com.mishlen.elastic_search.dto.LogSearchDTO;
import com.mishlen.elastic_search.services.EsService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final EsService esService;

    public ApiController(EsService esService) {
        this.esService = esService;
    }

    @PutMapping("/logs")
    public String addLog(@RequestBody LogRequestDTO requestObject) throws Exception {
        String id = UUID.randomUUID().toString();
        esService.updateLog(id, requestObject);
        return id;
    }

    @GetMapping("search")
    public String search(@RequestBody LogSearchDTO logSearchDTO, @RequestParam(value = "type", required = false, defaultValue = "false") Boolean zip) throws Exception {
        return esService.search(logSearchDTO, zip);
    }
}
