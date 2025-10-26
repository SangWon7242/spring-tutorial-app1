package com.sbs.tutorial.app1.domain.article.controller;

import com.sbs.tutorial.app1.base.config.security.dto.MemberContext;
import com.sbs.tutorial.app1.base.dto.RsData;
import com.sbs.tutorial.app1.domain.article.entity.Article;
import com.sbs.tutorial.app1.domain.article.input.ArticleForm.ArticleForm;
import com.sbs.tutorial.app1.domain.article.service.ArticleService;
import com.sbs.tutorial.app1.domain.fileUpload.entity.GenFile;
import com.sbs.tutorial.app1.domain.fileUpload.service.GenFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
@Slf4j
public class ArticleController {
  private final ArticleService articleService;
  private final GenFileService genFileService;

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/write")
  public String showWrite() {
    return "article/write";
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/write")
  @ResponseBody
  public String write(@AuthenticationPrincipal MemberContext memberContext,
                      @Valid ArticleForm articleForm,
                      MultipartRequest multipartRequest,
                      BindingResult bindingResult) {

    if(bindingResult.hasErrors()) {
      return "article/write";
    }

    Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

    Article article = articleService.write(memberContext.getId(), articleForm.getTitle(), articleForm.getContent());

    RsData<Map<String, GenFile>> saveFilesRsData = genFileService.saveFiles(article, fileMap);

    log.debug("saveFilesRsData : {}", saveFilesRsData);

    return "%d번 글이 작성되었습니다.".formatted(article.getId());
  }
}
