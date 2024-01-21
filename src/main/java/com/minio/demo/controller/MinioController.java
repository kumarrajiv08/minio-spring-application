package com.minio.demo.controller;

import com.minio.demo.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MinioController {

    @Autowired
    private MinioService minioService;

    @GetMapping("/presigned-url/upload/")
    public String getPresignedUrlForUpload(@RequestParam String filename) throws Exception {

        return minioService.getPresignedUrlForUpload(filename);
    }

    @GetMapping("/presigned-url/download/")
    public String getPresignedUrlForDownload(@RequestParam String filename) throws Exception {

        return minioService.getPresignedUrlForDownload(filename);
    }
}
