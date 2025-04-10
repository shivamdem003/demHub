package com.demHub.demHub.controller;

import com.demHub.demHub.Util.FileNode;
import jakarta.annotation.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/videos")
@CrossOrigin(origins = "*")
public class videoController {

    private static final String VIDEO_DIR = "D:/ANIME SERIES/Cartoon Collection"; // Root folder
    //D:/ANIME SERIES/Cartoon Collection

    @GetMapping
    public FileNode listFilesAndFolders(@RequestParam(value = "path", required = false) String path) {
        File directory = (path == null) ? new File(VIDEO_DIR) : new File(path);

        if (!directory.exists() || !directory.isDirectory()) {
            return null;
        }

        return getDirectoryStructure(directory);
    }

    private FileNode getDirectoryStructure(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            return null;
        }

        List<FileNode> children = new ArrayList<>();
        File[] files = folder.listFiles();

        if (files != null) {
            Arrays.sort(files); // Sort for consistent ordering
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively load subfolders and files
                    children.add(getDirectoryStructure(file));
                } else if (file.getName().endsWith(".mp4") || file.getName().endsWith(".mkv")){
                    children.add(new FileNode(file.getName(), file.getAbsolutePath(), false, null));
                }
            }
        }

        return new FileNode(folder.getName(), folder.getAbsolutePath(), true, children);
    }


    @GetMapping("/play")
    public ResponseEntity<InputStreamResource> streamVideo(
            @RequestParam String path,
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

        File file = new File(path);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        long fileSize = file.length();
        InputStream inputStream = new FileInputStream(file);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");

        if (rangeHeader == null) {
            // No range requested, send full file
            headers.setContentLength(fileSize);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaTypeFactory.getMediaType(file.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(new InputStreamResource(inputStream));
        }

        // Handle range request (Partial Content)
        List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
        HttpRange range = ranges.get(0);
        long rangeStart = range.getRangeStart(fileSize);
        long rangeEnd = Math.min(range.getRangeEnd(fileSize), fileSize - 1);
        long contentLength = rangeEnd - rangeStart + 1;

        headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize);
        headers.setContentLength(contentLength);
        headers.set(HttpHeaders.CONTENT_TYPE, "video/mp4");

        // Read requested range
        byte[] data = new byte[(int) contentLength];
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(rangeStart);
            raf.readFully(data);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(new InputStreamResource(new ByteArrayInputStream(data)));
    }

}
