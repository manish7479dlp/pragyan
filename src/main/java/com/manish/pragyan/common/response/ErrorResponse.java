package com.manish.pragyan.common.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ErrorResponse(boolean success, String message, Details details, Instant timestamp) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Details(int status, String title, String path, List<Map<String, String>> errors) {}

    public static ErrorResponse of(int status, String title, String message, String path,
            List<Map<String, String>> errors) {
        return new ErrorResponse(false, message, new Details(status, title, path, errors), Instant.now());
    }
}