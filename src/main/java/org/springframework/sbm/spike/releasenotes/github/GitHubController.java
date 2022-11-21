package org.springframework.sbm.spike.releasenotes.github;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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
    private final OkHttpClient okHttpClient;

    private final String repository;

    public GitHubController(String user, String token, String repository) {
        this.token = token;
        this.repository = repository;
        this.user = user;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        okHttpClient = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
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
            processError(response);
        }
        return response.body();
    }

    private static void processError(Response<IssueIdentifier> response) throws IOException {
        try(ResponseBody errorBody = response.errorBody()) {
            throw new RuntimeException("Cannot create issue: " + response.code() + "; " + ((errorBody != null) ? errorBody.string() : ""));
        }
    }

    public void shutdown() {
        okHttpClient.dispatcher().executorService().shutdown();
        okHttpClient.connectionPool().evictAll();
    }
}
