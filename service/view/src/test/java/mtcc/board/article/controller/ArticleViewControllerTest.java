package mtcc.board.article.controller;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ArticleViewControllerTest {
	RestClient restClient = RestClient.create("http://localhost:8085");

	@Test
	void viewTest() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		CountDownLatch latch = new CountDownLatch(10000);

		for (int i = 0; i < 10000; i++) {
			executorService.submit(() -> {
				restClient.post()
					.uri("/v1/article-views/articles/{articleId}/users/{userId}", 6L, 1L)
					.retrieve().body(Void.class);
				latch.countDown();
			});
		}

		latch.await();

		Long count = restClient.get()
			.uri("/v1/article-views/articles/{articleId}/count", 6L)
			.retrieve()
			.body(Long.class);

		System.out.println("count = " + count);
	}
}