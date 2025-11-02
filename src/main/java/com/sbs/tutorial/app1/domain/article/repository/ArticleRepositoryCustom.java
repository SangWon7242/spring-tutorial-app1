package com.sbs.tutorial.app1.domain.article.repository;

import com.sbs.tutorial.app1.domain.article.entity.Article;

import java.util.List;

public interface ArticleRepositoryCustom {
  List<Article> getQslArticleOrderByIdDesc();
}
