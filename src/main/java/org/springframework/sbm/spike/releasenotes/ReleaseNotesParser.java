package org.springframework.sbm.spike.releasenotes;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReleaseNotesParser {
    private final static Pattern RELEASE_VERSION_PATTERN = Pattern.compile(".*?");
    public List<ReleaseItem> parse(String path) throws IOException {
        String currentPath = new java.io.File(".").getCanonicalPath();
        File[] files = new File(currentPath + "/release-notes").listFiles();
        if (files == null) {
            return List.of();
        }
        return Stream.of(files)
                .sorted(Comparator.comparing(File::getName))
                .map(this::getFileContent)
                .flatMap(this::processFileContent)
                .collect(Collectors.toList());
    }

    private Stream<ReleaseItem> processFileContent(String fileContent) {
        final String releaseVersion = StringUtils.substringBetween(fileContent, "= Spring Boot ", " Release Notes");
        final String releaseVersionPrefix = (releaseVersion != null)? releaseVersion.replaceAll(" ", "-") + "-": "";
        return Stream.of(fileContent.split("===="))
                .flatMap(c -> Arrays.stream(c.split("===")))
                .filter(s -> !(s.contains("=="))) // remove title parts
                .map(String::trim)
                .filter(s -> s.contains("\n"))
                .map(s -> new ReleaseItem(releaseVersionPrefix, s));
    }
    private String getFileContent(File f) {
        try {
            return new String(Files.readAllBytes(Paths.get(f.getPath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
