package net.brabenetz.tools.smart.naming.core;

import java.io.File;

public class UploadedFileReference {

    private final File sourceFile;
    private final String fileId;
    private final String inlineImageDataUrl;

    public UploadedFileReference(File sourceFile, String fileId) {
        this(sourceFile, fileId, null);
    }

    public UploadedFileReference(File sourceFile, String fileId, String inlineImageDataUrl) {
        this.sourceFile = sourceFile;
        this.fileId = fileId;
        this.inlineImageDataUrl = inlineImageDataUrl;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public String getFileId() {
        return fileId;
    }

    public String getInlineImageDataUrl() {
        return inlineImageDataUrl;
    }

    public boolean usesFileUpload() {
        return fileId != null;
    }

    public String getOriginalFileName() {
        return sourceFile.getName();
    }
}