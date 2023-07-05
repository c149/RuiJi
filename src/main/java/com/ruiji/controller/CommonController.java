package com.ruiji.controller;

import com.ruiji.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Value("${ruiji.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> save_file(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String s = UUID.randomUUID().toString();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        fileName = s + suffix;
        File dir = new File(basePath);
        if(!dir.exists()) dir.mkdirs();
        File newFile = new File(basePath + fileName);
        file.transferTo(newFile);
        log.info("{}",fileName);
        return R.success(fileName);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse res) throws IOException {
        FileInputStream fis = new FileInputStream(basePath + name);
        res.setContentType("image/jpeg");
        ServletOutputStream os = res.getOutputStream();
        byte[] tem = new byte[1024];
        int len;
        while ((len = fis.read(tem)) != -1) {
            os.write(tem, 0 , len);
            os.flush();
        }
        os.close();
        fis.close();
    }
}
