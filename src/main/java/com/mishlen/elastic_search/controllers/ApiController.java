package com.mishlen.elastic_search.controllers;

import com.mishlen.elastic_search.dto.LogRequestDTO;
import com.mishlen.elastic_search.dto.LogSearchDTO;
import com.mishlen.elastic_search.services.EsService;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

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
        String logs = esService.search(logSearchDTO);
        if (zip) {
            String name = "logs.gz";
            FileOutputStream fos = new FileOutputStream(name);
            GZIPOutputStream gz = new GZIPOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(gz);
            oos.writeObject(logs);
            oos.close();
            // TODO: тут архив надо куда то загрузить, и отдавать ссылку
            return name;

        } else {
            return logs;
        }
    }
}
