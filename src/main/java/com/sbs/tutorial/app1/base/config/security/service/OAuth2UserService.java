package com.sbs.tutorial.app1.base.config.security.service;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {
  /*
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

    if (isNew(oauthType, oauthId)) {
      switch (oauthType) {
        case "KAKAO" -> {
          System.out.println(attributes);
          log.debug("attributes : " + attributes);

          Map attributesProperties = (Map) attributes.get("properties");
          Map attributesKakaoAcount = (Map) attributes.get("kakao_account");
          String nickname = (String) attributesProperties.get("nickname");
          String profileImage = (String) attributesProperties.get("profile_image");
          String email = "%s@kakao.com".formatted(oauthId);
          String username = "KAKAO_%s".formatted(oauthId);

          if ((boolean) attributesKakaoAcount.get("has_email")) {
            email = (String) attributesKakaoAcount.get("email");
          }

          member = Member.builder().email(email).username(username).password("").build();

          memberRepository.save(member);

          memberService.setProfileImgByUrl(member, profileImage);
        }
      }
    } else {
      member = memberRepository.findByUsername("%s_%s".formatted(oauthType, oauthId)).orElseThrow(MemberNotFoundException::new);
    }

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("member"));
    return new MemberContext(member, authorities, attributes, userNameAttributeName);
  }

  private boolean isNew(String oAuthType, String oAuthId) {
    return memberRepository.findByUsername("%s_%s".formatted(oAuthType, oAuthId)).isEmpty();
  }
*/
}