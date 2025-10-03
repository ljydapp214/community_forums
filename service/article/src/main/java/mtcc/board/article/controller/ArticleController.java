package mtcc.board.article.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mtcc.board.article.service.ArticleService;
import mtcc.board.article.service.request.ArticleCreateRequest;
import mtcc.board.article.service.request.ArticleUpdateRequest;
import mtcc.board.article.service.response.ArticleResponse;

@RestController
@RequiredArgsConstructor
public class ArticleController {
	private final ArticleService articleService;

	@GetMapping("/v1/articles/{articleId}")
	public ArticleResponse read(@PathVariable Long articleId) {
		return articleService.read(articleId);
	}

	@PostMapping("/v1/articles")
	public ArticleResponse create(@RequestBody ArticleCreateRequest request) {
		return articleService.create(request);
	}

	@PutMapping("/v1/articles/{articleId}")
	public ArticleResponse update(@PathVariable Long articleId, @RequestBody ArticleUpdateRequest request) {
		return articleService.update(articleId, request);
	}

	@DeleteMapping("/v1/articles/{articleId}")
	public void delete(@PathVariable Long articleId) {
		articleService.delete(articleId);
	}
}
