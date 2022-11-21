package org.springframework.sbm.spike.releasenotes;

import org.springframework.sbm.spike.releasenotes.github.GitHubController;
import org.springframework.sbm.spike.releasenotes.github.IssueIdentifier;

import java.io.IOException;
import java.util.*;

public class ReleaseNotesMain {

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Please use following args: OUT_DIRECTORY GITHUB_USER GITHUB_TOKEN GITHUB_REPO");
        }
        String targetDirectory = args[0];
        String user = args[1];
        String token = args[2];
        String repo = args[3];
        String currentPath = new java.io.File(".").getCanonicalPath();
        List<ReleaseItem> releaseItems = new ReleaseNotesParser().parse(currentPath + "/release-notes");
        new YamlBuilder().buildYamls(targetDirectory, releaseItems);

        GitHubController gitHubController = new GitHubController(user, token, repo);
        releaseItems.forEach(ri -> {
            try {
                IssueIdentifier id = gitHubController.createIssue("ReleaseNotes: " + ri.getTitle(), ri.getDescription(), user, List.of("SBM", "ReleaseNotes"));
                new YamlBuilder().updateAndRenameWithIssueId(targetDirectory, ri, id);
                System.out.println("Created: " + ri.getTitle() + "with id:" + id.getUrl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
