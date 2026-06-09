package net.brabenetz.tools.smart.naming.core;

import java.io.File;

public class UploadedFileReference {

    private final File sourceFile;
    private final String fileId;

    public UploadedFileReference(File sourceFile, String fileId) {
        this.sourceFile = sourceFile;
        this.fileId = fileId;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public String getFileId() {
        return fileId;
    }

    public String getOriginalFileName() {
        return sourceFile.getName();
    }
}