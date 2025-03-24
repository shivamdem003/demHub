package com.demHub.demHub.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//public class AVItoMP4Converter {
//    private static final String FFMPEG_PATH = "C:/ffmpeg/bin/ffmpeg.exe";
//    private static final String ROOT_FOLDER = "D:/ANIME SERIES/Cartoon Collection/Jinkalala/folder";
//
//    public static void main(String[] args) {
//        File folder = new File(ROOT_FOLDER);
//        if (folder.exists() && folder.isDirectory()) {
//            processFolder(folder);
//        } else {
//            System.out.println("Invalid folder path: " + ROOT_FOLDER);
//        }
//    }
//
//    private static void processFolder(File folder) {
//        File[] files = folder.listFiles();
//        if (files == null) return;
//
//        for (File file : files) {
//            if (file.isDirectory()) {
//                processFolder(file); // Recursively process subfolders
//            } else if (file.getName().toLowerCase().endsWith(".avi")) {
//                convertToMp4(file);
//            }
//        }
//    }
//
//    private static void convertToMp4(File aviFile) {
//        String mp4FilePath = aviFile.getAbsolutePath().replace(".avi", ".mp4");
//
//        ProcessBuilder builder = new ProcessBuilder(
//                FFMPEG_PATH, "-i", aviFile.getAbsolutePath(),
//                "-c:v", "libx264", "-preset", "fast", "-crf", "23",
//                "-c:a", "aac", "-b:a", "128k", mp4FilePath
//        );
//
//        // Redirect both output and error streams
//        builder.redirectErrorStream(true);
//
//        try {
//            System.out.println("Converting: " + aviFile.getAbsolutePath());
//            Process process = builder.start();
//
//            // Read FFmpeg output for debugging
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    System.out.println(line); // Print FFmpeg logs to debug issues
//                }
//            }
//
//            int exitCode = process.waitFor();
//            if (exitCode == 0) {
//                System.out.println("✅ Conversion successful: " + mp4FilePath);
//                aviFile.delete();
//                System.out.println("🗑 Deleted original file: " + aviFile.getAbsolutePath());
//            } else {
//                System.out.println("❌ Conversion failed for: " + aviFile.getAbsolutePath());
//            }
//        } catch (IOException | InterruptedException e) {
//            System.err.println("⚠ Error processing file: " + aviFile.getAbsolutePath());
//            e.printStackTrace();
//        }
//    }
//}

public class AVItoMP4Converter {
    private static final String FFMPEG_PATH = "C:/ffmpeg/bin/ffmpeg.exe";
    private static final String ROOT_FOLDER = "D:/ANIME SERIES/Cartoon Collection/Jinkalala/folder";
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors(); // Use available CPU cores

    public static void main(String[] args) {
        File rootFolder = new File(ROOT_FOLDER);
        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            System.out.println("❌ Invalid folder path: " + ROOT_FOLDER);
            return;
        }

        System.out.println("🔍 Scanning folder: " + ROOT_FOLDER);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        processFolder(rootFolder, executor);
        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            System.out.println("🎉 Conversion process completed!");
        } catch (InterruptedException e) {
            System.err.println("⚠ Error: Conversion process interrupted!");
        }
    }

    private static void processFolder(File folder, ExecutorService executor) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                processFolder(file, executor); // Recursively scan subfolders
            } else if (file.getName().toLowerCase().endsWith(".avi")) {
                executor.submit(() -> convertToMp4(file)); // Process each file in parallel
            }
        }
    }

    private static void convertToMp4(File aviFile) {
        String mp4FilePath = aviFile.getAbsolutePath().replace(".avi", ".mp4");
        ProcessBuilder builder = new ProcessBuilder(
                FFMPEG_PATH, "-i", aviFile.getAbsolutePath(),
                "-c:v", "libx264", "-preset", "fast", "-crf", "23",
                "-c:a", "aac", "-b:a", "128k", mp4FilePath
        );

        builder.redirectErrorStream(true);

        try {
            System.out.println("🎬 Converting: " + aviFile.getAbsolutePath());
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // Print FFmpeg logs
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ Conversion successful: " + mp4FilePath);
                if (aviFile.delete()) {
                    System.out.println("🗑 Deleted original file: " + aviFile.getAbsolutePath());
                } else {
                    System.err.println("⚠ Failed to delete: " + aviFile.getAbsolutePath());
                }
            } else {
                System.out.println("❌ Conversion failed for: " + aviFile.getAbsolutePath());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("⚠ Error processing file: " + aviFile.getAbsolutePath());
            e.printStackTrace();
        }
    }
}