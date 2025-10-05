package mtcc.board.comment.service;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class PageLimitCalculator {
	public static Long calculatePageLimit(long page, long pageSize, long movablePageCount) {
		return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1;
	}
}
