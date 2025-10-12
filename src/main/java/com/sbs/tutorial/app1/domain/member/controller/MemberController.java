package com.sbs.tutorial.app1.domain.member.controller;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.form.MemberJoinForm;
import com.sbs.tutorial.app1.domain.member.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

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

  @GetMapping("/profile")
  @PreAuthorize("isAuthenticated()")
  public String showProfile(Principal principal, Model model) {
    Member member = memberService.getMemberByUsername(principal.getName()).orElse(null);

    if(member != null) {
      model.addAttribute("member", member);
    }

    return "member/profile";
  }
}
