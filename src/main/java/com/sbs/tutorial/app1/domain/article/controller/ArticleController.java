package com.sbs.tutorial.app1.domain.article.controller;

import com.sbs.tutorial.app1.domain.article.input.ArticleForm.ArticleForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
@Slf4j
public class ArticleController {


  @PreAuthorize("isAuthenticated()")
  @GetMapping("/write")
  public String showWrite() {
    return "article/write";
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/write")
  @ResponseBody
  public String write(ArticleForm articleForm, MultipartRequest multipartRequest) {
    Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

    log.debug("fileMap : {}", fileMap);

    return "작성중";
  }
}
