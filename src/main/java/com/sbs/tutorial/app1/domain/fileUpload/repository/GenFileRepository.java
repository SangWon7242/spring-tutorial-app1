package com.sbs.tutorial.app1.domain.fileUpload.repository;

import com.sbs.tutorial.app1.domain.fileUpload.entity.GenFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenFileRepository extends JpaRepository<GenFile, Long> {
  List<GenFile> findByRelTypeCodeAndRelId(String relTypeCode, Long relId);

  List<GenFile> findByRelTypeCodeAndRelIdOrderByTypeCodeAscType2CodeAscFileNoAsc(String relTypeCode, Long relId);
}
