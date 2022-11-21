package org.springframework.sbm.spike.releasenotes.github;

public class IssueIdentifier {
    private String id;
    private String number;
    private String url;

    public IssueIdentifier() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
