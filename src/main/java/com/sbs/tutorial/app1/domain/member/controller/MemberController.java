package com.sbs.tutorial.app1.domain.member.controller;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.form.MemberJoinForm;
import com.sbs.tutorial.app1.domain.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
  public String join(MemberJoinForm memberJoinForm, HttpSession session) {
    String username = memberJoinForm.getUsername();

    Member oldMember = memberService.getMemberByUsername(username);

    if(oldMember != null) {
      return "redirect:/?errorMsg=Already exists username";
    }
    
    Member member = memberService.join(memberJoinForm);

    session.setAttribute("loginedMemberId", member.getId());

    return "redirect:/member/profile";
  }

  @GetMapping("/profile")
  public String showProfile(HttpSession session, Model model) {
    Long loginedMemberId = (Long) session.getAttribute("loginedMemberId");

    boolean isLogined = loginedMemberId != null;

    if(!isLogined) {
      return "redirect:/?errorMsg=Need to login!";
    }

    Member member = memberService.getMemberById(loginedMemberId);

    model.addAttribute("member", member);

    return "member/profile";
  }
}
