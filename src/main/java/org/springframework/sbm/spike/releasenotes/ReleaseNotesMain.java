package org.springframework.sbm.spike.releasenotes;

import org.springframework.sbm.spike.releasenotes.github.GitHubController;
import org.springframework.sbm.spike.releasenotes.github.IssueIdentifier;

import java.io.IOException;
import java.util.*;

public class ReleaseNotesMain {

    public static void main(String[] args) throws IOException {
        String targetDirectory = "/Users/ashakirin/DevResources/SpringBootMigrator/out";
        String currentPath = new java.io.File(".").getCanonicalPath();
        List<ReleaseItem> releaseItems = new ReleaseNotesParser().parse(currentPath + "/release-notes");
        new YamlBuilder().buildYamls(targetDirectory, releaseItems);

        GitHubController gitHubController = new GitHubController("ashakirin", "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", "sbm-parse-release-notes");
        releaseItems.forEach(ri -> {
            try {
                IssueIdentifier id = gitHubController.createIssue("ReleaseNotes: " + ri.getTitle(), ri.getDescription(), "ashakirin", List.of("SBM", "ReleaseNotes"));
                new YamlBuilder().updateAndRenameWithIssueId(targetDirectory, ri, id);
                System.out.println("Created: " + ri.getTitle() + "with id:" + id.getUrl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
