package com.sbs.tutorial.app1;

import com.sbs.tutorial.app1.domain.home.controller.HomeController;
import com.sbs.tutorial.app1.domain.member.controller.MemberController;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import com.sbs.tutorial.app1.domain.member.repository.MemberRepository;
import com.sbs.tutorial.app1.domain.member.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

  @Autowired
  private MemberRepository memberRepository;

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

  @Test
  @DisplayName("회원가입")
  @Rollback(false)
  void t05() throws Exception {
    // 파일 다운로드
    // 다운 받아야 하는 이미지 파일 : "https://picsum.photos/200/300"
    // 이미지 파일명 : "test-image.jpg"

    String imageUrl = "https://picsum.photos/200/300";
    String originalFileName = "test-image.jpg";

    // URL에서 이미지 다운로드
    byte[] imageBytes;
    try {
      URL url = new URL(imageUrl);
      imageBytes = url.openStream().readAllBytes();
      // url.openStream() : 인터넷에 연결해서 url 데이터의 접근
      // 이미지 파일의 모든 데이터가 byte[]로 저장
    } catch (IOException e) {
      // 오류 발생시 기본 테스트 데이터 사용
      imageBytes =  "test image content".getBytes();
    }

    MockMultipartFile profileImage = new MockMultipartFile(
        "profileImage",
        originalFileName,
        "image/jpeg",
        imageBytes
    );
    
    // 회원가입(MVC MOCK)
    // v1
    mvc.perform(multipart("/member/join")
            .file(profileImage)
            .param("username", "user5")
            .param("password", "1234")
            .param("email", "user5@test.com")
            .characterEncoding("UTF-8").with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/member/profile"))
        .andDo(print());

    /*
    // v2
    ResultActions resultActions = mvc.perform(multipart("/member/join")
            .file(profileImage)
            .param("username", "user5")
            .param("password", "1234")
            .param("email", "user5@test.com")
            .characterEncoding("UTF-8")).andDo(print());

    resultActions
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/member/profile"));
     */

    // 5번 회원이 생성, 테스트
    Member member = memberService.getMemberByUsername("user5")
        .orElseThrow(() -> new AssertionError("5번 회원이 존재하지 않습니다"));

    assertThat(member).isNotNull();
    assertThat(member.getId()).isEqualTo(5L);
    assertThat(member.getUsername()).isEqualTo("user5");
    assertThat(member.getEmail()).isEqualTo("user5@test.com");

    memberService.removeProfileImg(member);
  }
}
