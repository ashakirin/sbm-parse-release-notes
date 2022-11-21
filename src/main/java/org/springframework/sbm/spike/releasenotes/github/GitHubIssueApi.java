package org.springframework.sbm.spike.releasenotes.github;

import retrofit2.Call;
import retrofit2.http.*;

public interface GitHubIssueApi {
    @POST("/repos/{owner}/{repository}/issues")
    Call<IssueIdentifier> createIssue(
            @Header("Authorization") String credentials,
            @Path("owner") String owner,
            @Path("repository") String repository,
            @Body CreateIssueRequest request);
}
