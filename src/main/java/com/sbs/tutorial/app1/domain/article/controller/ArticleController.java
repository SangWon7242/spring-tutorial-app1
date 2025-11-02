package com.sbs.tutorial.app1.domain.article.controller;

import com.sbs.tutorial.app1.base.config.security.dto.MemberContext;
import com.sbs.tutorial.app1.base.dto.RsData;
import com.sbs.tutorial.app1.base.util.Ut;
import com.sbs.tutorial.app1.domain.article.entity.Article;
import com.sbs.tutorial.app1.domain.article.input.ArticleForm.ArticleForm;
import com.sbs.tutorial.app1.domain.article.service.ArticleService;
import com.sbs.tutorial.app1.domain.fileUpload.entity.GenFile;
import com.sbs.tutorial.app1.domain.fileUpload.service.GenFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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

    // log.debug("saveFilesRsData : {}", saveFilesRsData);

    String msg = "%d번 게시물이 작성되었습니다.".formatted(article.getId());
    
    // URL 파라미터에 한글을 사용하려면 URL 인코딩이 필요
    msg = Ut.url.encode(msg);

    return "redirect:/article/%d?msg=%s".formatted(article.getId(), msg);
  }

  @GetMapping("/detail/{id}")
  public String showDetail(Model model, @PathVariable Long id) {
    Article article = articleService.getForPrintArticleById(id);

    model.addAttribute("article", article);

    return "article/detail";
  }

  @GetMapping("/list")
  public String showList(Model model) {
    List<Article> articles = articleService.getArticles();

    if(articles.isEmpty()) {
      String msg = "게시물이 존재하지 않습니다.";

      // URL 파라미터에 한글을 사용하려면 URL 인코딩이 필요
      msg = Ut.url.encode(msg);

      return "redirect:/msg=%s".formatted(msg);
    }

    model.addAttribute("articles", articles);

    return "article/list";
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}/modify")
  public String showModify(@AuthenticationPrincipal MemberContext memberContext, Model model, @PathVariable Long id) {
    Article article = articleService.getForPrintArticleById(id);

    if(memberContext.getId() != article.getAuthor().getId()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시물 수정 권한이 없습니다.");
    }

    model.addAttribute("article", article);

    return "article/modify";
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/{id}/modify")
  public String modify(@AuthenticationPrincipal MemberContext memberContext,
                         Model model,
                         @PathVariable Long id,
                         @Valid ArticleForm articleForm,
                         MultipartRequest multipartRequest,
                         BindingResult bindingResult) {

    Article article = articleService.getForPrintArticleById(id);

    if(memberContext.getId() != article.getAuthor().getId()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시물 수정 권한이 없습니다.");
    }

    if(bindingResult.hasErrors()) {
      model.addAttribute("article", article);
      return "article/modify";
    }

    articleService.modify(article, articleForm.getTitle(), articleForm.getContent());

    Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
    RsData<Map<String, GenFile>> saveFilesRsData = genFileService.saveFiles(article, fileMap);
    log.debug("saveFilesRsData : {}", saveFilesRsData);
    
    String msg = "%d번 게시물이 수정되었습니다.".formatted(article.getId());

    // URL 파라미터에 한글을 사용하려면 URL 인코딩이 필요
    msg = Ut.url.encode(msg);

    return "redirect:/article/detail/%d?msg=%s".formatted(article.getId(), msg);
  }

  /*
  @GetMapping("/{id}/json/forDebug")
  @ResponseBody
  public Article showDetailJson(@PathVariable Long id) {
    return articleService.getForPrintArticleById(id);
  }
  */
}
