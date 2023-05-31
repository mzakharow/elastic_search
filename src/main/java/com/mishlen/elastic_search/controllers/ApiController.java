package com.mishlen.elastic_search.controllers;

import com.mishlen.elastic_search.dto.LogRequestDTO;
import com.mishlen.elastic_search.dto.LogResponseDTO;
import com.mishlen.elastic_search.dto.SearchRequestDTO;
import com.mishlen.elastic_search.dto.SearchResponseDTO;
import com.mishlen.elastic_search.services.EsService;
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

    @PutMapping("/logs")
    public ResponseEntity<LogResponseDTO> addLog(@RequestBody LogRequestDTO requestObject) throws Exception {
        LogResponseDTO response = new LogResponseDTO(esService.updateLog(requestObject));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<SearchResponseDTO> search(@RequestBody SearchRequestDTO searchRequestDTO, @RequestParam(value = "zip",
            required = false, defaultValue = "false") Boolean zip) throws Exception {
        return esService.search(searchRequestDTO, zip);
    }

    @RestControllerAdvice
    public class CustomExceptionHandler {

        @ExceptionHandler
        public ResponseEntity<String> handle(Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.I_AM_A_TEAPOT);
        }
    }
}
