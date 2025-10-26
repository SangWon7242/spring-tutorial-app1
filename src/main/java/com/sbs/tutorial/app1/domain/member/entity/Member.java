package com.sbs.tutorial.app1.domain.member.entity;

import com.sbs.tutorial.app1.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true) // BaseEntity의 toString()도 호출
public class Member extends BaseEntity {
  @Column(unique = true)
  private String username;
  private String password;
  @Column(unique = true)
  private String email;
  private String profileImg;

  public Member(long id) {
    super(id);
  }

  public String getProfileImgUrl() {
    if(profileImg == null) return null;

    return "/gen/" + profileImg;
  }
}
