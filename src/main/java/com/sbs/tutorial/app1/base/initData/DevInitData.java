package com.sbs.tutorial.app1.base.initData;

import com.sbs.tutorial.app1.domain.article.entity.Article;
import com.sbs.tutorial.app1.domain.article.service.ArticleService;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DevInitData {
  @Bean
  CommandLineRunner commandLineRunner(MemberService memberService, PasswordEncoder passwordEncoder, ArticleService articleService) {
    return args -> {
      String password = passwordEncoder.encode("1234");
      Member member1 = memberService.join("user1", password, "user1@test.com");
      memberService.setProfileImgByUrl(member1, "https://picsum.photos/200/300");

      Member member2 = memberService.join("user2", password, "user2@test.com");
      memberService.setProfileImgByUrl(member2, "https://picsum.photos/200/300");

      Article article = articleService.write(member1, "제목1", "내용1");
      articleService.addGenFileByUrl(article, "common", "inBody", 1, "https://picsum.photos/200/300");
      articleService.addGenFileByUrl(article, "common", "inBody", 2, "https://picsum.photos/200/300");
    };
  }
}
