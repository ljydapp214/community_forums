package mtcc.board.comment.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CommentPathTest {
	@Test
	void createChildCommentTest() {
		createChildCommentTest(CommentPath.create(""), null, "00000");

		createChildCommentTest(CommentPath.create("00000"), null, "0000000000");

		createChildCommentTest(CommentPath.create(""), "00000", "00001");

		createChildCommentTest(CommentPath.create("0000z"), "0000zabcdzzzzzzzzzzz", "0000zabce0");
	}

	void createChildCommentTest(CommentPath commentPath, String descendantsTopPath, String expected) {
		CommentPath childCommentPath = commentPath.createChildCommentPath(descendantsTopPath);
		assertThat(childCommentPath.getPath()).isEqualTo(expected);
	}

	@Test
	void createChildCommentPathIfMaxDepthTest() {
		assertThatThrownBy(() -> CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void createChildCommentPathIfChunkOverflowTest() {
		assertThatThrownBy(() -> CommentPath.create("").createChildCommentPath("zzzzz"));
	}
}