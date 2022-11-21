package org.springframework.sbm.spike.releasenotes;

public class ReleaseItem {
    private final String title;
    private final String description;
    private String filePath;
    private String fileName;

    public ReleaseItem(String versionPrefix, String content) {
        int pos = content.indexOf("\n");
        if (pos != -1) {
            title = versionPrefix + content.substring(0, pos).trim();
            description = content.substring(pos + 1).trim();
        } else {
            title = versionPrefix + "-" + content;
            description = "";
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String buildFileNameWithIssue(String issueId) {
        return fileName.replace("GH-Issue", "#" + issueId);
    }
}
