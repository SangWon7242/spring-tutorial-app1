package com.sbs.tutorial.app1.domain.member.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MemberJoinForm {
  String username;
  String password;
  String email;
  MultipartFile profileImg;
}
