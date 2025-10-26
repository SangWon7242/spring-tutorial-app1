package com.sbs.tutorial.app1.domain.article.service;

import com.sbs.tutorial.app1.domain.article.entity.Article;
import com.sbs.tutorial.app1.domain.article.input.ArticleForm.ArticleForm;
import com.sbs.tutorial.app1.domain.article.repository.ArticleRepository;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {
  private final ArticleRepository articleRepository;

  public Article write(Long authorId, String title, String content) {
    Article article = Article.builder()
        .author(new Member(authorId))
        .title(title)
        .content(content)
        .build();

    return articleRepository.save(article);
  }
}
