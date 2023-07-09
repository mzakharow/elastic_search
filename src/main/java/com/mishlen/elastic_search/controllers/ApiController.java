package com.mishlen.elastic_search.controllers;

import com.mishlen.elastic_search.dto.*;
import com.mishlen.elastic_search.services.EsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@ApiOperation("Products API")
@RestController
@RequestMapping("/api")
public class ApiController {

    private final EsService esService;

    public ApiController(EsService esService) {
        this.esService = esService;
    }

//    @ApiOperation(value = "Get a product by id", notes = "Returns a product as per the id")
    @PutMapping("/logs")
    public ResponseEntity<LogResponseDTO> addLog(@RequestBody LogRequestDTO requestObject) throws Exception {
        LogResponseDTO response = new LogResponseDTO(esService.updateLog(requestObject));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/search")
    public List<SearchResponseDTO> search(@RequestBody SearchRequestDTO searchRequestDTO, @RequestParam(value = "zip",
            required = false, defaultValue = "false") Boolean zip) throws Exception {
        return esService.search(searchRequestDTO, zip);
    }

    @PostMapping("/msearch")
    public List<SearchResponseDTO> msearch(@RequestBody RequestDTO searchRequestDTO, @RequestParam(value = "zip",
            required = false, defaultValue = "false") Boolean zip) throws Exception {
        return esService.msearch(searchRequestDTO, zip);
    }

    @RestControllerAdvice
    public class CustomExceptionHandler {

        @ExceptionHandler
        public ResponseEntity<String> handle(Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.I_AM_A_TEAPOT);
        }
    }
}
