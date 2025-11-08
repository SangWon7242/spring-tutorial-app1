package com.sbs.tutorial.app1.domain.article.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbs.tutorial.app1.domain.article.entity.Article;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sbs.tutorial.app1.domain.article.entity.QArticle.article;

@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Article> getQslArticleOrderByIdDesc() {
    return jpaQueryFactory
        .selectFrom(article) // SELECT * FROM article
        .orderBy(article.id.desc()) // ORDER BY id DESC;
        .fetch();
  }
}
