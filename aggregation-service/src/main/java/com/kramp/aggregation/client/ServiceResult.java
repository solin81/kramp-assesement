package com.kramp.aggregation.client;

public record ServiceResult<T>(T data, String warning) {
    public static <T> ServiceResult<T> available(T data) {
        return new ServiceResult<>(data, null);
    }

    public static <T> ServiceResult<T> unavailable(String warning) {
        return new ServiceResult<>(null, warning);
    }
}
