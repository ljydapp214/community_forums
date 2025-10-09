package mtcc.board.article.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mtcc.board.article.entity.ArticleViewCount;

@SpringBootTest
class ArticleViewCountBackUpProcessorTest {
	@Autowired
	ArticleViewCountBackUpRepository articleViewCountBackUpRepository;
	@PersistenceContext
	EntityManager entityManager;

	@Test
	@Transactional
	void updateViewCountTest() {
		// given
		articleViewCountBackUpRepository.save(
			ArticleViewCount.init(1L, 0L)
		);
		entityManager.flush();
		entityManager.clear();

		// when
		// 동시 요청 발생 시, 조회수가 낮은 숫자로 갱신되는 것을 방지
		int result1 = articleViewCountBackUpRepository.updateViewCount(1L, 100L);
		int result2 = articleViewCountBackUpRepository.updateViewCount(1L, 300L);
		int result3 = articleViewCountBackUpRepository.updateViewCount(1L, 200L);

		// then
		assertThat(result1).isEqualTo(1);
		assertThat(result2).isEqualTo(1);
		assertThat(result3).isEqualTo(0);

		ArticleViewCount articleViewCount = articleViewCountBackUpRepository.findById(1L).get();
		assertThat(articleViewCount.getViewCount()).isEqualTo(300L);
	}
}