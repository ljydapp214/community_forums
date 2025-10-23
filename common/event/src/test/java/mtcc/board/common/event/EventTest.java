package mtcc.board.common.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import mtcc.board.common.event.payload.ArticleCreatedEventPayload;

class EventTest {
	@Test
	void serde() {
		// given
		ArticleCreatedEventPayload payload = ArticleCreatedEventPayload.builder()
			.articleId(1L)
			.title("title")
			.content("content")
			.boardId(1L)
			.writerId(1L)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.boardArticleCount(23L)
			.build();

		Event<EventPayload> event = Event.of(
			1234L,
			EventType.ARTICLE_CREATED,
			payload
		);

		String json = event.toJson();
		System.out.println("json = " + json);

		// when
		Event<EventPayload> result = Event.fromJson(json);

		// then
		assertThat(result.eventId()).isEqualTo(event.eventId());
		assertThat(result.type()).isEqualTo(event.type());
		assertThat(result.payload()).isInstanceOf(payload.getClass());

		ArticleCreatedEventPayload resultPayload = (ArticleCreatedEventPayload)result.payload();
		assertThat(resultPayload.getArticleId()).isEqualTo(payload.getArticleId());
		assertThat(resultPayload.getTitle()).isEqualTo(payload.getTitle());
		assertThat(resultPayload.getCreatedAt()).isEqualTo(payload.getCreatedAt());
	}
}