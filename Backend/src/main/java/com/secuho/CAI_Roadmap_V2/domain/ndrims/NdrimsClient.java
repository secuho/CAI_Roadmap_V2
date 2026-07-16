package com.secuho.CAI_Roadmap_V2.domain.ndrims;

/**
 * V1의 Selenium 기반 NDRIMS 포털 크롤링(get_ndrims)을 대체할 추상화.
 * Phase 4에서 실제 크롤러(내부 마이크로서비스 호출)로 교체될 예정 — 그 전까지는 {@link MockNdrimsClient} 사용.
 */
public interface NdrimsClient {

    NdrimsVerificationResult verify(String studentId, String password);
}
