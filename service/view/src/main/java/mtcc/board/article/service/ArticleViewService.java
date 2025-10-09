package mtcc.board.article.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mtcc.board.article.repository.ArticleViewCountRepository;
import mtcc.board.article.repository.ArticleViewDistributedLockRepository;

@Service
@RequiredArgsConstructor
public class ArticleViewService {
	private static final int BACKUP_BATCH_SIZE = 100;
	private static final Duration TTL = Duration.ofMinutes(10);

	private final ArticleViewDistributedLockRepository articleViewDistributedLockRepository;
	private final ArticleViewCountRepository articleViewCountRepository;
	private final ArticleViewCountBackUpProcessor articleViewCountBackUpProcessor;

	public Long increase(Long articleId, Long userId) {
		if (!articleViewDistributedLockRepository.lock(articleId, userId, TTL)) {
			return articleViewCountRepository.read(articleId);
		}

		Long count = articleViewCountRepository.increase(articleId);

		if (count % BACKUP_BATCH_SIZE == 0) {
			articleViewCountBackUpProcessor.backUp(articleId, count);
		}
		return count;
	}

	public Long count(Long articleId) {
		return articleViewCountRepository.read(articleId);
	}
}
