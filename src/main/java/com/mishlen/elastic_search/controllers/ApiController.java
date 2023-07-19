package com.mishlen.elastic_search.controllers;

import com.mishlen.elastic_search.dto.*;
import com.mishlen.elastic_search.services.EsService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final EsService esService;

    public ApiController(EsService esService) {
        this.esService = esService;
    }

    @Hidden
    @Tag(name = "Hidden controller", description = "Test data filling in ElasticSearch")
    @PutMapping("/logs")
    public ResponseEntity<LogResponseDTO> addLog(@RequestBody LogRequestDTO requestObject) throws Exception {
        LogResponseDTO response = new LogResponseDTO(esService.updateLog(requestObject));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Find logs by parameters", tags = "search")
    @PostMapping("/search")
    public List<SearchResponseDTO> search(
            @RequestBody RequestDTO searchRequestDTO,
            @RequestParam(value = "zip", required = false, defaultValue = "false") Boolean zip
    ) throws Exception {
        return esService.search(searchRequestDTO, zip);
    }

    @RestControllerAdvice
    public class CustomExceptionHandler {

        @ExceptionHandler
        public ResponseEntity<String> handle(Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
