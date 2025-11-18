package mtcc.board.articleread.cache;

@FunctionalInterface
public interface OptimizedCacheOriginDataSupplier<T> {
	T get() throws Throwable;
}
