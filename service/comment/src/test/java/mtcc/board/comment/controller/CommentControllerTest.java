package mtcc.board.comment.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import mtcc.board.comment.service.response.CommentPageResponse;
import mtcc.board.comment.service.response.CommentResponse;

class CommentControllerTest {
	private final RestClient restClient = RestClient.create("http://localhost:8082");

	@Test
	void readAll() {
		CommentPageResponse response = restClient.get()
			.uri("/v1/comments?articleId=1&page=50000&pageSize=10")
			.retrieve()
			.body(CommentPageResponse.class);

		System.out.println("response.getCommentCount() = " + response.getCommentCount());
		for (CommentResponse comment : response.getComments()) {
			if (!comment.getCommentId().equals(comment.getParentCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}

		/**
		 * 1번 페이지 수행 결과
		 * comment.getCommentId() = 123693535103893504
		 * 	comment.getCommentId() = 123693535468797952
		 * 	comment.getCommentId() = 123693535527518208
		 * comment.getCommentId() = 123696314740150272
		 * 	comment.getCommentId() = 123696314773704717
		 * comment.getCommentId() = 123696314740150273
		 * 	comment.getCommentId() = 123696314777899028
		 * comment.getCommentId() = 123696314740150274
		 * 	comment.getCommentId() = 123696314773704705
		 * comment.getCommentId() = 123696314740150275
		 */
	}

	@Test
	void readAllInfiniteScroll() {
		List<CommentResponse> responses1 = restClient.get()
			.uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
			.retrieve()
			.body(new ParameterizedTypeReference<List<CommentResponse>>() {
			});

		System.out.println("firstPage");
		for (CommentResponse comment : responses1) {
			if (!comment.getCommentId().equals(comment.getParentCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}

		Long lastParentCommentId = responses1.getLast().getParentCommentId();
		Long lastCommentId = responses1.getLast().getCommentId();

		List<CommentResponse> responses2 = restClient.get()
			.uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
				.formatted(lastParentCommentId, lastCommentId))
			.retrieve()
			.body(new ParameterizedTypeReference<List<CommentResponse>>() {
			});

		System.out.println("secondPage");
		for (CommentResponse comment : responses2) {
			if (!comment.getCommentId().equals(comment.getParentCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}
	}
}