package com.demHub.demHub.Util;

import java.util.List;

public class FileNode {
    private String name;
    private String path;
    private boolean isFolder;
    private List<FileNode> children;

    public FileNode(String name, String path, boolean isFolder, List<FileNode> children) {
        this.name = name;
        this.path = path;
        this.isFolder = isFolder;
        this.children = children;
    }

    public String getName() { return name; }
    public String getPath() { return path; }
    public boolean isFolder() { return isFolder; }
    public List<FileNode> getChildren() { return children; }
}
