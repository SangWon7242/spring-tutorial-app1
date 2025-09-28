package com.sbs.tutorial.app1.domain.member.repository;

import com.sbs.tutorial.app1.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  // SELECT * FROM member WHERE username = ?
  Member findByUsername(String username);
}
