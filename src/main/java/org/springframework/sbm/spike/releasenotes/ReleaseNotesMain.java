package org.springframework.sbm.spike.releasenotes;

import org.springframework.sbm.spike.releasenotes.github.GitHubController;
import org.springframework.sbm.spike.releasenotes.github.IssueIdentifier;

import java.io.IOException;
import java.util.*;

public class ReleaseNotesMain {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Please use following args: GITHUB_USER GITHUB_TOKEN GITHUB_REPO");
            return;
        }
        String gitHubUser = args[0];
        String gitHubToken = args[1];
        String gitHubRepo = args[2];

        String currentPath = new java.io.File(".").getCanonicalPath();
        List<ReleaseItem> releaseItems = new ReleaseNotesParser().parse(currentPath + "/release-notes");
        String targetDirectory = currentPath + "/target/generated-sources/sbm-reports";
        new YamlBuilder().buildYamls(targetDirectory, releaseItems);

        GitHubController gitHubController = new GitHubController(gitHubUser, gitHubToken, gitHubRepo);
        try {
            for (ReleaseItem ri : releaseItems) {
                IssueIdentifier id = gitHubController.createIssue("ReleaseNotes: " + ri.getTitle(), ri.getDescription(), gitHubUser, List.of("SBM", "ReleaseNotes"));
                new YamlBuilder().updateAndRenameWithIssueId(targetDirectory, ri, id);
                System.out.println("Created: " + ri.getTitle() + " with id:" + id.getUrl());
            }
        } finally {
            gitHubController.shutdown();
        }
    }
}
