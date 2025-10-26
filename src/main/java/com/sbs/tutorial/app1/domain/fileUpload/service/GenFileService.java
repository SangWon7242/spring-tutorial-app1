package com.sbs.tutorial.app1.domain.fileUpload.service;

import com.sbs.tutorial.app1.base.util.Ut;
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

      // body__img__1
      // body, img, 1
      String[] inputNameBits = inputName.split("__");

      String typeCode = inputNameBits[0]; // 예 : bodyImg
      String type2Code = inputNameBits[1]; // 1
      String originFileName = multipartFile.getOriginalFilename();;
      String fileExt = Ut.file.getExt(originFileName);;
      String fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt);
      String fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt);
      int fileNo = Integer.parseInt(inputNameBits[2]);
      int fileSize = (int) multipartFile.getSize();
      String fileDir = relTypeCode + "/" + Ut.date.getCurrentDateFormatted("yyyy_MM_dd");

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
