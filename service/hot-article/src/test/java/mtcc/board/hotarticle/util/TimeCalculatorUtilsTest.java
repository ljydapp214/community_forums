package mtcc.board.hotarticle.util;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class TimeCalculatorUtilsTest {
	@Test
	void test() {
		Duration duration = TimeCalculatorUtils.calculateDurationToMidnight();
		System.out.println(duration.getSeconds() / 60);
	}
}