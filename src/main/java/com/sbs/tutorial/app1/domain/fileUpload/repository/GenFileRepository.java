package com.sbs.tutorial.app1.domain.fileUpload.repository;

import com.sbs.tutorial.app1.domain.fileUpload.entity.GenFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenFileRepository extends JpaRepository<GenFile, Long> {
}
