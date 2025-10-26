package com.sbs.tutorial.app1.domain.article.input.ArticleForm;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleForm {
  @NotEmpty
  private String title;
  @NotEmpty
  private String content;
}
