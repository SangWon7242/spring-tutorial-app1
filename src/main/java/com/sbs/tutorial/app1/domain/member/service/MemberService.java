package com.sbs.tutorial.app1.domain.member.service;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.form.MemberJoinForm;
import com.sbs.tutorial.app1.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
  @Value("${custom.genFileDirPath}")
  private String genFileDirPath;
  private final MemberRepository memberRepository;

  public Member getMemberByUsername(String username) {
    return memberRepository.findByUsername(username);
  }

  public Member join(MemberJoinForm memberJoinForm) {
    // 프로필 이미지가 저장될 경로
    String profileImgRelPath = "member/" + UUID.randomUUID() + ".png";
    // 예시 : member/1234567890.png

    File profileImgFile = new File(genFileDirPath + "/" + profileImgRelPath);
    // genFileDirPath(c:/spring-temp/app1)
    // profileImgFile(c:/spring-temp/app1/member/1234567890.png)

    // canExecute : 폴더가 없으면 폴더 생성
    if(!profileImgFile.canExecute()) {
      profileImgFile.mkdirs();
    }

    MultipartFile profileImg = memberJoinForm.getProfileImg();

    try {
      // transferTo : 파일 저장
      // profileImgFile : 파일 저장 경로
      profileImg.transferTo(profileImgFile);
    } catch (IOException e) {
      e.printStackTrace();
    }

    Member member = Member.builder()
        .username(memberJoinForm.getUsername())
        .password("{noop}" + memberJoinForm.getPassword())
        .email(memberJoinForm.getEmail())
        .profileImg(profileImgRelPath)
        .build();

    memberRepository.save(member);

    return member;
  }
}
