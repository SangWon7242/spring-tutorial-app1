package com.sbs.tutorial.app1.base.config.security.service;

import com.sbs.tutorial.app1.base.config.security.dto.MemberContext;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 사용자 조회
    Member member = memberRepository.findByUsername(username).get();

    // 권한 설정
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("member"));

    return new MemberContext(member, authorities);
  }
}
