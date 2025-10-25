package com.sbs.tutorial.app1.domain.article.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/write")
  public String showWrite() {
    return "article/write";
  }
}
