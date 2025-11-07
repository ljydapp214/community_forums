package mtcc.board.articleread.cache;

import java.time.Duration;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.ToString;
import mtcc.board.common.serializer.DataSerializer;

@Getter
@ToString
public class OptimizedCache {
	private final String data;
	// Logical TTL
	private final LocalDateTime expiredAt;

	private OptimizedCache(String data, LocalDateTime expiredAt) {
		this.data = data;
		this.expiredAt = expiredAt;
	}

	public static OptimizedCache of(Object data, Duration logicalTTL) {
		return new OptimizedCache(
			DataSerializer.serialize(data),
			LocalDateTime.now().plus(logicalTTL)
		);
	}

	@JsonIgnore
	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiredAt);
	}

	public <T> T parseData(Class<T> clazz) {
		return DataSerializer.deserialize(this.data, clazz);
	}
}
