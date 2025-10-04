package mtcc.board.article.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class PageLimitCalculatorTest {
	private void calculatePageLimitTest(long page, long pageSize, long movablePageCount, long expected) {
		Long limit = PageLimitCalculator.calculatePageLimit(page, pageSize, movablePageCount);
		assertThat(limit).isEqualTo(expected);
	}

	@Test
	void calculatePageLimitTest() {
		calculatePageLimitTest(1L, 30L, 10L, 301L);
		calculatePageLimitTest(7L, 30L, 10L, 301L);
		calculatePageLimitTest(10L, 30L, 10L, 301L);
		calculatePageLimitTest(11L, 30L, 10L, 601L);
		calculatePageLimitTest(12L, 30L, 10L, 601L);
	}
}
