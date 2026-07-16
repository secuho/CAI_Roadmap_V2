package com.secuho.CAI_Roadmap_V2.domain.ndrims;

public record NdrimsVerificationResult(boolean success, String message) {

    public static NdrimsVerificationResult ok() {
        return new NdrimsVerificationResult(true, "ok");
    }

    public static NdrimsVerificationResult failure(String message) {
        return new NdrimsVerificationResult(false, message);
    }
}
