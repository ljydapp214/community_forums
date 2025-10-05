package mtcc.board.comment.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mtcc.board.comment.entity.Comment;
import mtcc.board.comment.repository.CommentRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
	@InjectMocks
	CommentService commentService;
	@Mock
	CommentRepository commentRepository;

	@Test
	@DisplayName("삭제할 댓글이 자식이 있으면, 삭제 표시만 한다.")
	void deleteShouldMarkDeletedIfhasChildren() {
		Long articleId = 1L;
		Long commentId = 2L;
		Comment comment = createComment(articleId, commentId);
		given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
		given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(2L);

		commentService.delete(commentId);

		verify(comment).delete();
	}

	@Test
	@DisplayName("삭제할 댓글이 자식이 없으면, 댓글을 삭제한다.")
	void deleteShouldDeleteChildOnlyIfNotDeleteParent() {
		Long articleId = 1L;
		Long commentId = 2L;
		Long parentCommentId = 1L;

		Comment comment = createComment(articleId, commentId, parentCommentId);
		given(comment.isRoot()).willReturn(false);

		Comment parentComment = mock(Comment.class);
		given(parentComment.getDeleted()).willReturn(false);

		given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
		given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(1L);

		given(commentRepository.findById(parentCommentId)).willReturn(Optional.of(parentComment));

		commentService.delete(commentId);

		verify(commentRepository).delete(comment);
		verify(parentComment, never()).delete();
	}

	@Test
	@DisplayName("하위 댓글이 삭제되고, 삭제된 부모면, 재귀적으로 모두 삭제한다")
	void deleteShouldDeleteAllRecursivelyIfDeletedParent() {
		Long articleId = 1L;
		Long commentId = 2L;
		Long parentCommentId = 1L;

		Comment comment = createComment(articleId, commentId, parentCommentId);
		given(comment.isRoot()).willReturn(false);

		Comment parentComment = createComment(articleId, parentCommentId);
		given(parentComment.isRoot()).willReturn(true);
		given(parentComment.getDeleted()).willReturn(true);

		given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
		given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(1L);

		given(commentRepository.findById(parentCommentId)).willReturn(Optional.of(parentComment));
		given(commentRepository.countBy(articleId, parentCommentId, 2L)).willReturn(1L);

		commentService.delete(commentId);

		verify(commentRepository).delete(comment);
		verify(parentComment, never()).delete();
	}

	private Comment createComment(Long articleId, Long commentId) {
		Comment comment = mock(Comment.class);
		given(comment.getArticleId()).willReturn(articleId);
		given(comment.getCommentId()).willReturn(commentId);
		return comment;
	}

	private Comment createComment(Long articleId, Long commentId, Long parentCommentId) {
		Comment comment = createComment(articleId, commentId);
		given(comment.getParentCommentId()).willReturn(parentCommentId);
		return comment;
	}
}