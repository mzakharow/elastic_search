package com.mishlen.elastic_search.controllers;

import com.mishlen.elastic_search.dto.LogRequestDTO;
import com.mishlen.elastic_search.services.EsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
//    public List<EsService.Log> search(@RequestParam("query") String query, @RequestParam("level") String level) throws Exception {
    public List<EsService.Log> search(@RequestParam("query") String query) throws Exception {
        return esService.search(query);
    }
}
