package mtcc.board.articleread.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mtcc.board.articleread.cache.OptimizedCacheable;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewClient {
	private RestClient restClient;
	@Value("${endpoints.board-view-service.url}")
	private String viewServiceUrl;

	@PostConstruct
	public void init() {
		this.restClient = RestClient.create(viewServiceUrl);
	}

	// 레디스에서 데이터 조회
	// 레디스에 데이터가 있으면, 그 데이터를 그대로 바로 반환
	// 레디스에 데이터가 없다면, Count 메서드 내부 로직을 호출 -> article-view-service 로 원본 데이터 요청
	// -> 응답받은 데이터를 redis 에 저장한 후, 데이터를 반환
	// @Cacheable(key = "#articleId", value = "articleViewCount")
	// type: cache 의 타입, ttlSeconds: cache 의 유효기간(초), methods 의 파라미터: cache key 생성 시, args 로 사용
	@OptimizedCacheable(type = "articleViewCount", ttlSeconds = 1)
	public long count(Long articleId) {
		log.info("[ViewClient.count] articleId={}", articleId);
		try {
			return restClient.get()
				.uri("/v1/article-views/articles/{articleId}/count", articleId)
				.retrieve()
				.body(Long.class);
		} catch (Exception e) {
			log.error("[ViewClient.count] articleId={}", articleId, e);
			return 0L;
		}
	}
}
