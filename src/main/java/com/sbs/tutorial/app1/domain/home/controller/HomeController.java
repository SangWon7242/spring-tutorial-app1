package com.sbs.tutorial.app1.domain.home.controller;


import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HomeController {
  private final MemberService memberService;

  @RequestMapping("/")
  public String main(Principal principal, Model model) {
    if(principal == null) {
      return "home/main";
    }

    System.out.println("principal : " + principal);

    Member loginedMember = null;
    String loginedMemberProfileImgUrl = null;

    if(principal != null && principal.getName() != null) {
      loginedMember = memberService.getMemberByUsername(principal.getName()).orElse(null);
    }

    System.out.println(loginedMember.getProfileImg());

    if(loginedMember != null) {
      loginedMemberProfileImgUrl = "/gen/" + loginedMember.getProfileImg();
    }

    model.addAttribute("loginedMember", loginedMember);
    model.addAttribute("loginedMemberProfileImgUrl", loginedMemberProfileImgUrl);

    return "home/main";
  }

  @RequestMapping("/test/upload")
  public String upload() {
    return "home/test/upload";
  }
}
