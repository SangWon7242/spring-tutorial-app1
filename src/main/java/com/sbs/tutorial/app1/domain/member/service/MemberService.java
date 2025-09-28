package com.sbs.tutorial.app1.domain.member.service;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.form.MemberJoinForm;
import com.sbs.tutorial.app1.domain.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
  @Value("${custom.genFileDirPath}")
  private String genFileDirPath;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member getMemberByUsername(String username) {
    return memberRepository.findByUsername(username).orElse(null);
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

    String passwordClearText = memberJoinForm.getPassword();
    String password = passwordEncoder.encode(passwordClearText);

    Member member = Member.builder()
        .username(memberJoinForm.getUsername())
        .password(password)
        .email(memberJoinForm.getEmail())
        .profileImg(profileImgRelPath)
        .build();

    memberRepository.save(member);

    return member;
  }

  public Member getMemberById(Long id) {
    return memberRepository.findById(id).orElse(null);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 사용자 조회
    Member member = memberRepository.findByUsername(username).get();
    
    // 권한 설정
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("member"));

    return new User(member.getUsername(), member.getPassword(), authorities);
  }
}
