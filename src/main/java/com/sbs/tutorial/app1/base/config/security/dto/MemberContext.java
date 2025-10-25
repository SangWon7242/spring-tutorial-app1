package com.sbs.tutorial.app1.base.config.security.dto;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class MemberContext extends User implements OAuth2User {
  private final Long id;
  private final String email;

  @Setter
  private final LocalDateTime modifyDate;
  private final String profileImgUrl;
  private Map<String, Object> attributes;
  private String userNameAttributeName;

  public MemberContext(Member member, List<GrantedAuthority> authorities) {
    super(member.getUsername(), member.getPassword(), authorities);
    this.id = member.getId();
    this.email = member.getEmail();
    this.modifyDate = member.getModifyDate();
    this.profileImgUrl = member.getProfileImg();
  }

  public MemberContext(Member member, List<GrantedAuthority> authorities, Map<String, Object> attributes, String userNameAttributeName) {
    this(member, authorities);
    this.attributes = attributes;
    this.userNameAttributeName = userNameAttributeName;
  }

  // getAuthorities : 권한 정보
  @Override
  public Set<GrantedAuthority> getAuthorities() {
    return super.getAuthorities().stream().collect(Collectors.toSet());
  }
  
  // getAttributes : 사용자 정보가 map형태로 들어옴
  // 사용자 프로필 정보를 추출 가능
  @Override
  public Map<String, Object> getAttributes() {
    return this.attributes;
  }
  
  // 로그인한 사용자의 고유 번호를 반환
  @Override
  public String getName() {
    if (userNameAttributeName != null && attributes != null) {
      Object nameAttribute = getAttribute(userNameAttributeName);
      if (nameAttribute != null) {
        return nameAttribute.toString();
      }
    }
    // fallback to username from User class
    return super.getUsername();
  }

  public String getProfileImgRedirectUrl() {
    return "/member/profile/img/" + getId() + "?cacheKey=" + getModifyDate().toString();
  }
}
