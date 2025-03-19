//package com.demHub.demHub.controller;
//
//import jakarta.annotation.Resource;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.http.MediaType;
//import org.springframework.http.MediaTypeFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//
//@RestController
//@RequestMapping("/videos")
//@CrossOrigin(origins = "*")
//public class VideoController {
//
//    private static final String VIDEO_DIR = "C:/videos"; // Change this path to your video directory
//
//    @GetMapping
//    public ResponseEntity<List<String>> listVideos() {
//        File folder = new File(VIDEO_DIR);
//        String[] files = folder.list((dir, name) -> name.endsWith(".mp4") || name.endsWith(".mkv"));
//
//        if (files == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(Arrays.asList(files));
//    }
//
//    @GetMapping("/{filename}")
//    public ResponseEntity<FileSystemResource> streamVideo(@PathVariable String filename) throws IOException {
//        File file = new File(VIDEO_DIR, filename);
//        if (!file.exists()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        FileSystemResource fileSystemResource = new FileSystemResource(file);
//        return ResponseEntity.ok()
//                .contentType(MediaTypeFactory.getMediaType(file.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM))
//                .body(fileSystemResource);
//    }
//}
