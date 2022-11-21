package org.springframework.sbm.spike.releasenotes;

import org.springframework.sbm.spike.releasenotes.github.IssueIdentifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class YamlBuilder {

    public void buildYamls(String targetDirectory, List<ReleaseItem> releaseItems) {
        try (InputStream templateStream = YamlBuilder.class.getResourceAsStream("/release-item-template.yaml")) {
            if (templateStream == null) {
                throw new IllegalStateException("Cannot load yaml template for release notes");
            }
            String template = new String(templateStream.readAllBytes());

            File targetDirectoryFile = new File(targetDirectory);
            if (!targetDirectoryFile.exists()){
                targetDirectoryFile.mkdirs();
            }

            releaseItems.forEach(ri -> {
                String filePath = createReleaseItemYaml(targetDirectory, template, ri);
                ri.setFileName(filePath);

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String createReleaseItemYaml(String targetDirectory, String template, ReleaseItem ri) {
        try {
            String fileName = "GH-Issue-" + ri.getTitle().replaceAll("[^a-zA-Z0-9-_\\.]", "_");
            String fullPath = targetDirectory + File.separator + fileName;
            String content = template
                    .replace("--TITLE--", ri.getTitle())
                    .replace("--DESCRIPTION--", ri.getDescription());
            Files.write(Paths.get(fullPath), content.getBytes());
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAndRenameWithIssueId(String targetDirectory, ReleaseItem ri, IssueIdentifier issueId) throws IOException {
        Path fullPath = Paths.get(targetDirectory + File.separator + ri.getFileName());

        String content = new String(Files.readAllBytes(fullPath));
        String newContent = content
                .replaceAll("--GH-ISSUE--", issueId.getUrl())
                .replaceAll("--GH-ISSUE-NUMBER-", issueId.getNumber());
        Files.writeString(fullPath, newContent);

        String newPath = targetDirectory + File.separator + ri.buildFileNameWithIssue(issueId.getNumber());
        Files.move(fullPath, Paths.get(newPath));
    }
}
