package org.springframework.sbm.spike.releasenotes.github;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.sbm.spike.releasenotes.ReleaseNotesMain;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

public class GitHubController {
    private static final Logger logger = LoggerFactory.getLogger(GitHubController.class);

    static final String BASE_URL = "https://api.github.com";
    public static final int DEFAULT_SLEEP_TIME = 30000;
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
        Response<IssueIdentifier> response = sendCreateIssueRequest(title, body, assignee, labels);
        if (!response.isSuccessful()) {
            if (response.code() == 403) {
                return retryWithDelayForRateLimit(title, body, assignee, labels, response.headers().get("Retry-After"));
            }
            processError(response);
        }
        return response.body();
    }

    private IssueIdentifier retryWithDelayForRateLimit(String title, String body, String assignee, List<String> labels, String retryAfter) {
        try {
            long delayTimeMillis = (retryAfter != null)? Long.parseLong(retryAfter) : DEFAULT_SLEEP_TIME;
            logger.warn("Rate limit reached. Will retry after: " + delayTimeMillis);
            Thread.sleep(delayTimeMillis);
            Response<IssueIdentifier> response = sendCreateIssueRequest(title, body, assignee, labels);
            if (!response.isSuccessful()) {
                processError(response);
            }
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Response<IssueIdentifier> sendCreateIssueRequest(String title, String body, String assignee, List<String> labels) throws IOException {
        CreateIssueRequest issueRequest = new CreateIssueRequest(title, body, assignee, labels);
        String credentials = Credentials.basic(user, token);
        Call<IssueIdentifier> callResponse = gitHubIssueApi.createIssue(credentials, user, repository, issueRequest);
        return callResponse.execute();
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
