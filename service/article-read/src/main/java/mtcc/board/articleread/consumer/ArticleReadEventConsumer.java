package mtcc.board.articleread.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mtcc.board.articleread.service.ArticleReadService;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventPayload;
import mtcc.board.common.event.EventType;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleReadEventConsumer {
	private final ArticleReadService articleReadService;

	// 컨슈머는 쓰기 트래픽에 대해서 작업
	// API 는 조회 트래픽에 대해서 작업
	// 두 개의 작업을 각각의 서비스로 분리하는 방식
	//
	@KafkaListener(
		topics = {
			EventType.Topic.BOARD_ARTICLE,
			EventType.Topic.BOARD_COMMENT,
			EventType.Topic.BOARD_LIKE,
			EventType.Topic.BOARD_VIEW
		}
	)
	public void consume(String message, Acknowledgment ack) {
		log.info("[ArticleReadEventConsumer.consume] message={}", message);
		Event<EventPayload> event = Event.fromJson(message);
		if (event != null) {
			articleReadService.handleEvent(event);
		}
		ack.acknowledge();
	}

}
