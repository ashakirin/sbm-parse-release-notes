package org.springframework.sbm.spike.releasenotes.github;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

public class GitHubController {
    static final String BASE_URL = "https://api.github.com";
    private final String user;
    private final String token;
    private final GitHubIssueApi gitHubIssueApi;

    private final String repository;

    private final Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public GitHubController(String user, String token, String repository) {
        this.token = token;
        this.repository = repository;
        this.user = user;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gitHubIssueApi = retrofit.create(GitHubIssueApi.class);
    }

    public IssueIdentifier createIssue(
            String title,
            String body,
            String assignee,
            List<String> labels) throws IOException {
        CreateIssueRequest issueRequest = new CreateIssueRequest(title, body, assignee, labels);
        String credentials = Credentials.basic(user, token);
        Call<IssueIdentifier> callResponse = gitHubIssueApi.createIssue(credentials, user, repository, issueRequest);
        Response<IssueIdentifier> response = callResponse.execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("Cannot create issue: " + response.code() + "; " + response.errorBody().string());
        }
        return response.body();
    }
}
