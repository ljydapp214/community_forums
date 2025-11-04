package mtcc.board.articleread.config;

import java.time.Duration;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
public class CacheConfig {
	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
		return RedisCacheManager.builder(factory)
			.withInitialCacheConfigurations(
				Map.of(
					"articleViewCount", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(1L))
				)
			)
			.build();
	}

}
