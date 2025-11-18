package mtcc.board.articleread.cache;

import java.util.Arrays;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.common.serializer.DataSerializer;

@Component
@RequiredArgsConstructor
public class OptimizedCacheManager {
	private static final String DELIMITER = "::";

	private final StringRedisTemplate redisTemplate;
	private final OptimizedCacheLockProvider lockProvider;

	private String generateKey(String type, Object[] args) {
		return type
			+ DELIMITER
			+ String.join(DELIMITER, Arrays.stream(args)
			.map(String::valueOf)
			.toList());
	}

	private Object refresh(
		OptimizedCacheOriginDataSupplier<?> originDataSupplier,
		String key,
		long ttlSeconds
	) throws Throwable {
		Object result = originDataSupplier.get();

		OptimizedCacheTTL optimizedCacheTTL = OptimizedCacheTTL.of(ttlSeconds);
		OptimizedCache optimizedCache = OptimizedCache.of(result, optimizedCacheTTL.getLogicalTTL());

		redisTemplate.opsForValue()
			.set(key, DataSerializer.serialize(optimizedCache), optimizedCacheTTL.getPhysicalTTL());

		return result;
	}

	public Object process(
		String type,
		long ttlSeconds,
		Object[] args,
		Class<?> clazz,
		OptimizedCacheOriginDataSupplier<?> originDataSupplier
	) throws Throwable {
		String key = generateKey(type, args);

		String cachedData = redisTemplate.opsForValue().get(key);
		// cache 에 데이터 없으면, 서비스에서 조회(refresh) 후 캐시에 저장
		if (cachedData == null) {
			return refresh(originDataSupplier, key, ttlSeconds);
		}

		OptimizedCache optimizedCache = DataSerializer.deserialize(cachedData, OptimizedCache.class);
		// cache 에 들어있던 데이터가 null 인 경우, 서비스에서 조회(refresh) 후 캐시에 저장
		if (optimizedCache == null) {
			return refresh(originDataSupplier, key, ttlSeconds);
		}

		// cache 에 들어있던 데이터의 logicalTTL 이 유효하다면, cache 를 반환
		if (!optimizedCache.isExpired()) {
			return optimizedCache.parseData(clazz);
		}

		if (!lockProvider.lock(key)) {
			// lock 획득 실패 -> 다른 쓰레드가 이미 refresh 중이므로, 기존 cache 를 반환
			return optimizedCache.parseData(clazz);
		}

		try {
			return refresh(originDataSupplier, key, ttlSeconds);
		} finally {
			lockProvider.unlock(key);
		}
	}
}
