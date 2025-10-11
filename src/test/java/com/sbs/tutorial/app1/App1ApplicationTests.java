package com.sbs.tutorial.app1;

import com.sbs.tutorial.app1.domain.home.controller.HomeController;
import com.sbs.tutorial.app1.domain.member.controller.MemberController;
import com.sbs.tutorial.app1.domain.member.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
// @Transactional : JUnit 테스트 코드가 실행되면, 이 메서드가 끝나고 DB가 자동으로 롤백되게끔
@Transactional
@ActiveProfiles({"base-addi", "test"})
class App1ApplicationTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private MemberService memberService;

  @Test
  @DisplayName("메인화면에서는 안녕이 나와야 한다.")
  void t01() throws Exception {
    // WHEN :
    // GET /
    ResultActions resultActions = mvc
        .perform(get("/")).andDo(print());

    // THEN
    // 안녕
    resultActions.andExpect(status().is2xxSuccessful()) // 상태코드 확인
        .andExpect(handler().handlerType(HomeController.class)) // 컨트롤러 확인
        .andExpect(handler().methodName("main")) // 메서드명 확인
        .andExpect(content().string(containsString("안녕"))); // 내용 확인
    /*
    200 : 성공
    300 : 리다이렉트
    400 : 클라이언트 잘못
    500 : 서버 잘못
    */
  }

  @Test
  @DisplayName("회원의 수")
  @Rollback(false)
  void t02() {
    long count = memberService.count();
    assertThat(count).isGreaterThan(0);
  }

  @Test
  @DisplayName("user1로 로그인 후 프로필페이지에 접속하면 user1의 이메일이 보여야 한다.")
  void t03() throws Exception {
    // mocMvc로 로그인 처리

    // WHEN:
    // GET : /member/login
    ResultActions resultActions = mvc
        .perform(get("/member/profile")
            .with(user("user1").password("1234").roles("user")))
        .andDo(print());

    // THEN
    resultActions.andExpect(status().is2xxSuccessful()) // 상태코드 확인
        .andExpect(handler().handlerType(MemberController.class))
        .andExpect(handler().methodName("showProfile")) // 메서드명 확인
        .andExpect(content().string(containsString("user1@test.com"))); // 내용 확인
  }

  @Test
  @DisplayName("user4로 로그인 후 프로필페이지에 접속하면 user4의 이메일이 보여야 한다.")
  void t04() throws Exception {
    // mocMvc로 로그인 처리

    // WHEN:
    // GET : /member/login
    ResultActions resultActions = mvc
        .perform(get("/member/profile")
            .with(user("user4").password("1234").roles("user")))
        .andDo(print());

    // THEN
    resultActions.andExpect(status().is2xxSuccessful()) // 상태코드 확인
        .andExpect(handler().handlerType(MemberController.class))
        .andExpect(handler().methodName("showProfile")) // 메서드명 확인
        .andExpect(content().string(containsString("user4@test.com"))); // 내용 확인
  }
}
