package com.sbs.tutorial.app1.domain.fileUpload.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/upload")
public class FileUploadController {
  @RequestMapping("")
  @ResponseBody
  public String upload(@RequestParam("img1") MultipartFile file) {
    String uploadDir = "c:/spring-temp/app1"; // 저장 디렉토리
    String fileName = "1.png";
    
    // 파일 저장 경로 생성
    File directory = new File(uploadDir);

    if(!directory.exists()) {
      directory.mkdirs(); // 경로가 없으면 디렉토리 생성
    }

    // 파일 저장
    File saveFile = new File(uploadDir, fileName);

    try {
      file.transferTo(saveFile); // 파일 저장
      return "파일 업로드 완료 : " + saveFile.getAbsolutePath(); // 파일 경로 반환
    } catch (IOException e) {
      e.printStackTrace();
      return "파일 업로드 실패 : " + e.getMessage();
    }
  }
}
