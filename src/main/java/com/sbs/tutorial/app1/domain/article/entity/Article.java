package com.sbs.tutorial.app1.domain.article.entity;

import com.sbs.tutorial.app1.base.entity.BaseEntity;
import com.sbs.tutorial.app1.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Article extends BaseEntity {
  @ManyToOne
  private Member author;
  private String title;
  private String content;
}
