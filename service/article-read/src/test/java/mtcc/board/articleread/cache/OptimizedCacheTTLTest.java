package mtcc.board.articleread.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class OptimizedCacheTTLTest {
	@Test
	void ofTest() {
		// given
		long ttlSeconds = 10;

		// when
		OptimizedCacheTTL optimizedCacheTTL = OptimizedCacheTTL.of(ttlSeconds);

		// then
		assertThat(optimizedCacheTTL.getLogicalTTL()).isEqualTo(Duration.ofSeconds(ttlSeconds));
		assertThat(optimizedCacheTTL.getPhysicalTTL()).isEqualTo(
			Duration.ofSeconds(ttlSeconds).plusSeconds(OptimizedCacheTTL.PHYSICAL_TTL_DELAY_SECONDS)
		);
	}
}