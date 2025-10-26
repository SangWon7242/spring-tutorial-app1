package com.sbs.tutorial.app1.domain.fileUpload.service;

import com.sbs.tutorial.app1.domain.article.entity.Article;
import com.sbs.tutorial.app1.domain.fileUpload.entity.GenFile;
import com.sbs.tutorial.app1.domain.fileUpload.repository.GenFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenFileService {
  private final GenFileRepository genFileRepository;

  public void saveFiles(Article article, Map<String, MultipartFile> fileMap) {
    String relTypeCode = "article";
    long relId = article.getId();

    for (String inputName : fileMap.keySet()) {
      MultipartFile multipartFile = fileMap.get(inputName);

      String typeCode = "common"; // 파일 타입
      String type2Code = "inBody"; // 세부 타입
      String fileExt = "jpg"; // 파일 확장자
      String fileExtTypeCode = "img"; // 확장자 타입
      String fileExtType2Code = "jpg"; // 세부 확장자 타입
      int fileNo = 1; // 파일 번호
      int fileSize = 1000; // 파일 크기
      String fileDir = "article/2025_10_26"; // 저장 디렉토리
      String originFileName = "??"; // 원본 파일명

      GenFile genFile = GenFile
          .builder()
          .relTypeCode(relTypeCode)
          .relId(relId)
          .typeCode(typeCode)
          .type2Code(type2Code)
          .fileExtTypeCode(fileExtTypeCode)
          .fileExtType2Code(fileExtType2Code)
          .fileNo(fileNo)
          .fileSize(fileSize)
          .fileDir(fileDir)
          .fileExt(fileExt)
          .originFileName(originFileName)
          .build();

      genFileRepository.save(genFile);
    }
  }
}
