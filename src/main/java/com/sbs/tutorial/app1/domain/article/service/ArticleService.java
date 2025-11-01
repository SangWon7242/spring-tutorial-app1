package com.sbs.tutorial.app1.domain.article.service;

import com.sbs.tutorial.app1.domain.article.entity.Article;
import com.sbs.tutorial.app1.domain.article.repository.ArticleRepository;
import com.sbs.tutorial.app1.domain.fileUpload.service.GenFileService;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {
  private final ArticleRepository articleRepository;
  private final GenFileService genFileService;

  public Article write(Long authorId, String title, String content) {
    return write(new Member(authorId), title, content);
  }

  public Article write(Member author, String title, String content) {
    Article article = Article
        .builder()
        .author(author)
        .title(title)
        .content(content)
        .build();

    return articleRepository.save(article);
  }

  public Article getArticleById(Long id) {
    return articleRepository.findById(id).orElse(null);
  }

  public void addGenFileByUrl(Article article, String typeCode, String type2Code, int fileNo, String url) {
    genFileService.addGenFileByUrl("article", article.getId(), typeCode, type2Code, fileNo, url);
  }

  public Article getForPrintArticleById(Long id) {
    Article article = getArticleById(id);

    article.getExtra().put("age", 22);

    return article;
  }
}
