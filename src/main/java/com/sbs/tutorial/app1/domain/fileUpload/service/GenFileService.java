package com.sbs.tutorial.app1.domain.fileUpload.service;

import com.sbs.tutorial.app1.base.config.base.AppConfig;
import com.sbs.tutorial.app1.base.dto.RsData;
import com.sbs.tutorial.app1.base.util.Ut;
import com.sbs.tutorial.app1.domain.article.entity.Article;
import com.sbs.tutorial.app1.domain.fileUpload.entity.GenFile;
import com.sbs.tutorial.app1.domain.fileUpload.repository.GenFileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenFileService {
  private final GenFileRepository genFileRepository;

  private String getCurrentDirName(String relTypeCode) {
    return relTypeCode + "/" + Ut.date.getCurrentDateFormatted("yyyy_MM_dd");
  }

  public RsData<Map<String, GenFile>> saveFiles(Article article, Map<String, MultipartFile> fileMap) {
    String relTypeCode = "article";
    long relId = article.getId();

    // 내가 만든 파일을 genFileIds 에 넣겠다.
    Map<String, GenFile> genFileIds = new HashMap<>();

    // inputName : common__inBody__1
    for (String inputName : fileMap.keySet()) {
      MultipartFile multipartFile = fileMap.get(inputName);

      if(multipartFile.isEmpty()) continue;

      String[] inputNameBits = inputName.split("__");

      String typeCode = inputNameBits[0]; // 예 : bodyImg
      String type2Code = inputNameBits[1]; // 1
      String originFileName = multipartFile.getOriginalFilename();;
      String fileExt = Ut.file.getExt(originFileName);;
      String fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt);
      String fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt);
      int fileNo = Integer.parseInt(inputNameBits[2]);
      int fileSize = (int) multipartFile.getSize();

      String fileDir = getCurrentDirName(relTypeCode);

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

      genFile = save(genFile);

      String filePath = AppConfig.GEN_FILE_DIR_PATH + "/" + fileDir + "/" + genFile.getFileName();

      File file = new File(filePath);
      file.getParentFile().mkdirs();

      try {
        multipartFile.transferTo(file);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      genFileIds.put(inputName, genFile);
    }

    return new RsData<>("S-1", "파일을 업로드했습니다.", genFileIds);
  }

  public GenFile save(GenFile genFile) {
    Optional<GenFile> existGenFile = genFileRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(genFile.getRelTypeCode(), genFile.getRelId(), genFile.getTypeCode(), genFile.getType2Code(), genFile.getFileNo());

    if(existGenFile.isPresent()) {
      GenFile oldGenFile = existGenFile.get();
      deleteFileFromStorage(oldGenFile);

      oldGenFile.merge(genFile);

      genFileRepository.save(genFile);
      return oldGenFile;
    }

    genFileRepository.save(genFile);

    return genFile;
  }
  
  // 기존 파일 제거
  private void deleteFileFromStorage(GenFile oldGenFile) {
    new File(oldGenFile.getFilePath()).delete();
  }

  public void addGenFileByUrl(String relTypeCode, Long relId, String typeCode, String type2Code, int fileNo, String url) {
    String fileDir = getCurrentDirName(relTypeCode);

    String downFilePath = Ut.file.downloadImg(url, AppConfig.GEN_FILE_DIR_PATH + "/" + fileDir + "/" + UUID.randomUUID());

    File downloadedFile = new File(downFilePath);

    String originFileName = downloadedFile.getName();
    String fileExt = Ut.file.getExt(originFileName);;
    String fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt);
    String fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt);
    int fileSize = 0;
    
    // 저장하려는 파일에 파일 사이즈 구하는 방법
    try {
      fileSize = (int) Files.size(Paths.get(downFilePath));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

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

    genFile = save(genFile);

    String filePath = AppConfig.GEN_FILE_DIR_PATH + "/" + fileDir + "/" + genFile.getFileName();

    File file = new File(filePath);
    file.getParentFile().mkdirs();

    downloadedFile.renameTo(file);
  }

  // article과 연관된 genFile(생성 된 이미지 파일)을 Map형태로 반환
  public Map<String, GenFile> getRelGenFileMap(Article article) {
    List<GenFile> genFiles = genFileRepository.findByRelTypeCodeAndRelIdOrderByTypeCodeAscType2CodeAscFileNoAsc("article", article.getId());

    return genFiles.stream()
        .collect(Collectors.toMap(
            genFile -> genFile.getTypeCode() + "__" + genFile.getType2Code() + "__" + genFile.getFileNo(),
            genFile -> genFile,
            (genFile1, genFile2) -> genFile1,
            LinkedHashMap::new
        ));
  }
}
