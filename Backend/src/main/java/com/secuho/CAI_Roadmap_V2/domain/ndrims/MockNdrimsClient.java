package com.secuho.CAI_Roadmap_V2.domain.ndrims;

import org.springframework.stereotype.Component;

@Component
public class MockNdrimsClient implements NdrimsClient {

    @Override
    public NdrimsVerificationResult verify(String studentId, String password) {
        return NdrimsVerificationResult.ok();
    }
}
