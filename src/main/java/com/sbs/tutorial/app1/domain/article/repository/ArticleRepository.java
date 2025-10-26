package com.sbs.tutorial.app1.domain.article.repository;

import com.sbs.tutorial.app1.domain.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
