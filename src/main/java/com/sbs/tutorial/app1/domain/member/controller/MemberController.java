package com.sbs.tutorial.app1.domain.member.controller;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.form.MemberJoinForm;
import com.sbs.tutorial.app1.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
  @ResponseBody
  public Member join(MemberJoinForm memberJoinForm) {
    String username = memberJoinForm.getUsername();

    Member oldMember = memberService.getMemberByUsername(username);

    if(oldMember != null) {
      return null; // 이미 가입된 회원
    }
    
    Member member = memberService.join(memberJoinForm);

    return member;
  }
}
