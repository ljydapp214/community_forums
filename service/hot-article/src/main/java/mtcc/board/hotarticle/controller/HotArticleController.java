package mtcc.board.hotarticle.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mtcc.board.hotarticle.service.HotArticleService;
import mtcc.board.hotarticle.service.response.HotArticleResponse;

@RestController
@RequiredArgsConstructor
public class HotArticleController {
	private final HotArticleService hotArticleService;

	@GetMapping("/v1/hot-articles/articles/date/{dateString}")
	public List<HotArticleResponse> readAll(
		@PathVariable("dateString") String dateStr
	) {
		return hotArticleService.readAll(dateStr);
	}
}
