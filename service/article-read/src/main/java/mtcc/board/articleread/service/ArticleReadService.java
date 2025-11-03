package mtcc.board.articleread.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mtcc.board.articleread.client.ArticleClient;
import mtcc.board.articleread.client.CommentClient;
import mtcc.board.articleread.client.LikeClient;
import mtcc.board.articleread.client.ViewClient;
import mtcc.board.articleread.repository.ArticleIdListRepository;
import mtcc.board.articleread.repository.ArticleQueryModel;
import mtcc.board.articleread.repository.ArticleQueryModelRepository;
import mtcc.board.articleread.repository.BoardArticleCountRepository;
import mtcc.board.articleread.service.event.handler.EventHandler;
import mtcc.board.articleread.service.response.ArticleReadPageResponse;
import mtcc.board.articleread.service.response.ArticleReadResponse;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventPayload;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleReadService {
	private final ArticleClient articleClient;
	private final CommentClient commentClient;
	private final LikeClient likeClient;
	private final ViewClient viewClient;
	private final ArticleQueryModelRepository articleQueryModelRepository;
	private final ArticleIdListRepository articleIdListRepository;
	private final BoardArticleCountRepository boardArticleCountRepository;
	private final List<EventHandler> eventHandlers;

	private Optional<ArticleQueryModel> fetch(Long articleId) {
		Optional<ArticleClient.ArticleResponse> read = articleClient.read(articleId);
		long commentCount = commentClient.count(articleId);
		long likeCount = likeClient.count(articleId);

		Optional<ArticleQueryModel> articleQueryModel = read.map(articleResponse ->
			ArticleQueryModel.create(articleResponse, commentCount, likeCount));
		articleQueryModel.ifPresent(
			articleQuery -> articleQueryModelRepository.create(articleQuery, Duration.ofDays(1L)));
		log.info("[ArticleReadService.fetch] fetch data. articleId={}, isPresent={}", articleId,
			articleQueryModel.isPresent());

		return articleQueryModel;
	}

	public ArticleReadResponse readResponse(Long articleId) {
		ArticleQueryModel articleQueryModel = articleQueryModelRepository.read(articleId)
			.or(() -> fetch(articleId))
			.orElseThrow();

		return ArticleReadResponse.from(articleQueryModel, viewClient.count(articleId));
	}

	public void handleEvent(Event<EventPayload> event) {
		for (EventHandler handler : eventHandlers) {
			if (handler.supports(event)) {
				handler.handle(event);
			}
		}
	}

	private List<Long> readAllArticleIds(Long boardId, Long page, Long pageSize) {
		List<Long> articleIds = articleIdListRepository.readAll(boardId, (page - 1) * pageSize, pageSize);
		if (pageSize == articleIds.size()) {
			log.info(
				"[ArticleReadService.readAllArticleIds] read articleIds from cache. boardId={}, page={}, pageSize={}",
				boardId, page, pageSize);
			return articleIds;
		}

		log.info("[ArticleReadService.readAllArticleIds] read articleIds from DB. boardId={}, page={}, pageSize={}",
			boardId, page, pageSize);
		return articleClient.readAll(boardId, page, pageSize).getArticles().stream()
			.map(ArticleClient.ArticleResponse::getArticleId)
			.toList();
	}

	private List<ArticleReadResponse> readAll(List<Long> articleIds) {
		Map<Long, ArticleQueryModel> articleQueryModelMap = articleQueryModelRepository.readAll(articleIds);
		return articleIds.stream()
			.map(articleId -> articleQueryModelMap.containsKey(articleId) ?
				articleQueryModelMap.get(articleId) : fetch(articleId).orElse(null))
			.filter(Objects::nonNull)
			.map(articleQueryModel ->
				ArticleReadResponse.from(articleQueryModel, viewClient.count(articleQueryModel.getArticleId()))
			).toList();
	}

	private Long getArticleCount(Long boardId) {
		Long redisCached = boardArticleCountRepository.read(boardId);
		if (redisCached != null) {
			return redisCached;
		}

		long serverResponse = articleClient.count(boardId);
		boardArticleCountRepository.createOrUpdate(boardId, serverResponse);
		return serverResponse;
	}

	public ArticleReadPageResponse readAll(Long boardId, Long page, Long pageSize) {
		List<Long> articleIds = readAllArticleIds(boardId, page, pageSize);
		List<ArticleReadResponse> result = readAll(articleIds);
		Long articleCount = getArticleCount(boardId);

		return ArticleReadPageResponse.of(result, articleCount);
	}

	private List<Long> readAllInfiniteScrollArticleIds(Long boardId, Long lastArticleId, Long pageSize) {
		List<Long> articleIds = articleIdListRepository.readAllInfiniteScroll(boardId, lastArticleId, pageSize);
		if (pageSize == articleIds.size()) {
			log.info("[ArticleReadService.readAllInfiniteScrollArticleIds]"
					+ " read articleIds from cache. boardId={}, lastArticleId={}, pageSize={}",
				boardId, lastArticleId, pageSize);
			return articleIds;
		}

		log.info("[ArticleReadService.readAllInfiniteScrollArticleIds]"
				+ " read articleIds from DB. boardId={}, lastArticleId={}, pageSize={}",
			boardId, lastArticleId, pageSize);
		return articleClient.readAllInfiniteScroll(boardId, lastArticleId, pageSize).stream()
			.map(ArticleClient.ArticleResponse::getArticleId)
			.toList();
	}

	public List<ArticleReadResponse> readAllInfiniteScroll(Long boardId, Long lastArticleId, Long pageSize) {
		return readAll(readAllInfiniteScrollArticleIds(boardId, lastArticleId, pageSize));
	}
}
