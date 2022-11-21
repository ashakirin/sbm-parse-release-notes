package org.springframework.sbm.spike.releasenotes.github;

import java.util.ArrayList;
import java.util.List;

public class CreateIssueRequest {
    private String title;
    private String body;
    private String assignee;
    private List<String> labels = new ArrayList<>();

    public CreateIssueRequest() {
    }

    public CreateIssueRequest(String title, String body, String assignee, List<String> labels) {
        this.title = title;
        this.body = body;
        this.assignee = assignee;
        this.labels = labels;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
