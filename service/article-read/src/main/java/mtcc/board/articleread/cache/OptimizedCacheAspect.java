package mtcc.board.articleread.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class OptimizedCacheAspect {
	private final OptimizedCacheManager optimizedCacheManager;

	private OptimizedCacheable findAnnotation(ProceedingJoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		return signature.getMethod().getAnnotation(OptimizedCacheable.class);
	}

	private Class<?> findReturnType(ProceedingJoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		return signature.getReturnType();
	}

	@Around("@annotation(OptimizedCacheable)")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		OptimizedCacheable cacheable = findAnnotation(joinPoint);
		return optimizedCacheManager.process(
			cacheable.type(),
			cacheable.ttlSeconds(),
			joinPoint.getArgs(),
			findReturnType(joinPoint),
			joinPoint::proceed
		);
	}
}
