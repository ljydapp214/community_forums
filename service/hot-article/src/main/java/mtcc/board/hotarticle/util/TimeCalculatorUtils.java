package mtcc.board.hotarticle.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TimeCalculatorUtils {
	public static Duration calculateDurationToMidnight() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime midnight = now.plusDays(1).with(LocalTime.MIDNIGHT);
		return Duration.between(now, midnight);
	}
}
