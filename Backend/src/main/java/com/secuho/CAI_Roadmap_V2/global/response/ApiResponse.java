package com.secuho.CAI_Roadmap_V2.global.response;

public record ApiResponse<T>(String status, String message, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", null, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("success", message, data);
    }

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>("fail", message, null);
    }
}
