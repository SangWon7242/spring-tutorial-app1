package com.sbs.tutorial.app1.base.config.security.service;

import com.sbs.tutorial.app1.base.config.security.dto.MemberContext;
import com.sbs.tutorial.app1.base.config.security.exception.OAuthTypeMatchNotFoundException;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.exception.MemberNotFoundException;
import com.sbs.tutorial.app1.domain.member.repository.MemberRepository;
import com.sbs.tutorial.app1.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j // 로깅 기능을 제공
public class OAuth2UserService extends DefaultOAuth2UserService {

  private final MemberRepository memberRepository;
  private final MemberService memberService;

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
    Map<String, Object> attributes = oAuth2User.getAttributes();

    String oauthId = oAuth2User.getName();

    Member member = null;
    String oauthType = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

    if (!"KAKAO".equals(oauthType)) {
      throw new OAuthTypeMatchNotFoundException();
    }

    // OAuth ID로 가입된 회원이 없는 경우
    // 로그인시 회원가입을 실시(새 회원 생성)
    if (isNew(oauthType, oauthId)) {
      switch (oauthType) {
        case "KAKAO" -> {

          System.out.println(attributes);
          log.debug("attributes : " + attributes);

          Map attributesProperties = (Map) attributes.get("properties");
          Map attributesKakaoAccount = (Map) attributes.get("kakao_account");
          String nickname = (String) attributesProperties.get("nickname");
          String profileImage = (String) attributesProperties.get("profile_image");
          String email = "%s@kakao.com".formatted(oauthId);
          String username = "KAKAO_%s".formatted(oauthId);

          if ((boolean) attributesKakaoAccount.get("has_email")) {
            email = (String) attributesKakaoAccount.get("email");
          }

          member = Member.builder().email(email).username(username).password("").build();

          memberRepository.save(member); // 프로필 이미지 없이 회원 생성

          memberService.setProfileImgByUrl(member, profileImage);
        }
      }
    } else { // 가입한 회원이 있으면 회원 데이터 가져옴
      member = memberRepository.findByUsername("%s_%s".formatted(oauthType, oauthId)).orElseThrow(MemberNotFoundException::new);
    }

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("member"));
    return new MemberContext(member, authorities, attributes, userNameAttributeName);
  }

  private boolean isNew(String oAuthType, String oAuthId) {
    return memberRepository.findByUsername("%s_%s".formatted(oAuthType, oAuthId)).isEmpty();
  }
}