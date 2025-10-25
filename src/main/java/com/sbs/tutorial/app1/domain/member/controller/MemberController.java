package com.sbs.tutorial.app1.domain.member.controller;

import com.sbs.tutorial.app1.base.config.security.dto.MemberContext;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.form.MemberJoinForm;
import com.sbs.tutorial.app1.domain.member.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
  private final MemberService memberService;

  @GetMapping("/join")
  public String showJoin() {
    return "member/join";
  }

  @PostMapping("/join")
  public String join(MemberJoinForm memberJoinForm, HttpServletRequest req, MultipartFile profileImage) {
    String username = memberJoinForm.getUsername();
    String password = memberJoinForm.getPassword();

    Member oldMember = memberService.getMemberByUsername(username).orElse(null);

    if(oldMember != null) {
      return "redirect:/?errorMsg=Already exists username";
    }

    Member member = memberService.join(memberJoinForm, profileImage);

    try {
      req.login(username, password); // 로그인 처리
      System.out.println("로그인 성공");
    } catch (ServletException e) {
      throw new RuntimeException(e);
    }

    return "redirect:/member/profile";
  }

  @GetMapping("/login")
  public String showLogin() {
    return "member/login";
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/profile")
  public String showProfile() {
    return "member/profile";
  }

  @GetMapping("/profile/img/{id}")
  public ResponseEntity<Object> showProfileImg(@PathVariable("id") Long id) {
    String profileImgUrl = memberService.getMemberById(id).getProfileImgUrl();

    if(profileImgUrl == null) {
      profileImgUrl = "https://placehold.co/100x100?text=U_U";
    }
    
    // 캐시 거는 방법
    return ResponseEntity
        .status(HttpStatus.FOUND)
        .cacheControl(CacheControl
            .maxAge(1, TimeUnit.HOURS) 
            .cachePublic()
            .immutable())
        .location(URI.create(profileImgUrl))
        .build();
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/modify")
  public String showModify() {
    return "member/modify";
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/modify")
  public String modify(
      @AuthenticationPrincipal MemberContext memberContext,
      MultipartFile profileImage) {

    Member member = memberService.getMemberById(memberContext.getId());

    memberService.modify(member, profileImage);

    return "member/modify";
  }
}
