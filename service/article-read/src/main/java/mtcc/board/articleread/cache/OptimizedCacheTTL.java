package mtcc.board.articleread.cache;

import java.time.Duration;

import lombok.Getter;

@Getter
public class OptimizedCacheTTL {
	public static final long PHYSICAL_TTL_DELAY_SECONDS = 5L;

	private final Duration logicalTTL;
	private final Duration physicalTTL;

	private OptimizedCacheTTL(Duration logicalTTL, Duration physicalTTL) {
		this.logicalTTL = logicalTTL;
		this.physicalTTL = physicalTTL;
	}

	public static OptimizedCacheTTL of(long ttlSeconds) {
		Duration logicalTTL = Duration.ofSeconds(ttlSeconds);
		return new OptimizedCacheTTL(logicalTTL, logicalTTL.plusSeconds(PHYSICAL_TTL_DELAY_SECONDS));
	}
}
