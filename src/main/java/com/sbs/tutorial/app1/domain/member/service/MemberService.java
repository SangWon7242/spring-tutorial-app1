package com.sbs.tutorial.app1.domain.member.service;

import com.sbs.tutorial.app1.base.util.Ut;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.form.MemberJoinForm;
import com.sbs.tutorial.app1.domain.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
  @Value("${custom.genFileDirPath}")
  private String genFileDirPath;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Optional<Member> getMemberByUsername(String username) {
    return memberRepository.findByUsername(username);
  }

  private String getCurrentProfileImgDirName() {
    return "member/" + Ut.date.getCurrentDateFormatted("yyyy_MM_dd");
  }


  public Member join(MemberJoinForm memberJoinForm, MultipartFile profileImage) {
    // 프로필 이미지가 저장될 경로
    String profileImgDirName = getCurrentProfileImgDirName();
    String ext = Ut.file.getExt(profileImage.getOriginalFilename());
    String fileName = UUID.randomUUID() + "." + ext;
    String profileImgDirPath = genFileDirPath + "/" + profileImgDirName;
    // profileImgDirPath -> c:/spring-temp/app1/member
    String profileImgFilePath = profileImgDirPath + "/" + fileName;
    // profileImgFilePath -> c:/spring-temp/app1/member/123.png

    new File(profileImgDirPath).mkdirs(); // 관련된 폴더가 없으면 만들어준다.

    try {
      profileImage.transferTo(new File(profileImgFilePath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    String profileImgRelPath = profileImgDirName + "/" + fileName;
    // member/123.png

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

  public Member join(String username, String password, String email) {
    Member member = Member.builder()
        .username(username)
        .password(password)
        .email(email)
        .build();

    memberRepository.save(member);

    return member;
  }

  // SELECT COUNT(*) FROM `member`;
  public long count() {
    return memberRepository.count();
  }

  public void removeProfileImg(Member member) {
    if(member.getProfileImg() == null || member.getProfileImg().isEmpty()) return;

    String profileImgPath = genFileDirPath + "/" + member.getProfileImg();
    File file = new File(profileImgPath);

    if(file.exists()) file.delete();
  }

  public void setProfileImgByUrl(Member member, String url) {
    String filePath = Ut.file.downloadImg(url, genFileDirPath + "/" + getCurrentProfileImgDirName() + "/" + UUID.randomUUID());
    member.setProfileImg(getCurrentProfileImgDirName() + "/" + new File(filePath).getName());
    memberRepository.save(member);
  }
}
