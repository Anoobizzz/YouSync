package yousync.sources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yousync.domain.PlaylistRequest;
import yousync.domain.PlaylistResponse;
import yousync.domain.Song;
import yousync.domain.youtube.PlaylistItemsResponse;
import yousync.domain.youtube.PlaylistItemsResponse.PlaylistItem;
import yousync.domain.youtube.Token;
import yousync.ui.YouTubeTabController;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class YouTubeSource implements MusicSource {
    private static final Logger LOG = LoggerFactory.getLogger(YouTubeSource.class);

    private static final String GOOGLE_TOKEN_REQUEST_URL = "https://www.googleapis.com/oauth2/v4/token";
    private static final String GOOGLE_LOOPBACK_OAUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String YOUTUBE_BASE_URI = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_BASE_API_URI = "https://www.googleapis.com/youtube/v3";

    private static Token token = new Token();

    @Autowired
    private Client client;
    @Autowired
    private YouTubeTabController controller;

    public void authorize() {
        String page = client.target(GOOGLE_LOOPBACK_OAUTH_URL)
                .queryParam("client_id", controller.getClientIdBoxText())
                .queryParam("redirect_uri", "http://127.0.0.1:8080/loopback")
                .queryParam("response_type", "code")
                .queryParam("scope", "https://www.googleapis.com/auth/youtube.force-ssl")
                .request().get().readEntity(String.class);
        controller.displayWebAuthenticationWindow(page);
    }

    @Override
    public boolean isAuthorized() {
        if (isNotEmpty(token.getAccessToken()) && isNotEmpty(token.getRefreshToken())) {
            if (token.getTokenAcquireTime() + token.getExpiresIn() * 1000 < currentTimeMillis()) {
                refreshToken();
            }
            return true;
        }
        return false;
    }

    private void getToken(String code) {
        Form input = new Form();
        input.param("client_id", controller.getClientIdBoxText());
        input.param("client_secret", controller.getClientSecretBoxText());
        input.param("redirect_uri", "http://127.0.0.1:8080/loopback");
        input.param("grant_type", "authorization_code");
        input.param("code", code);

        Entity<Form> entity = Entity.entity(input, MediaType.APPLICATION_FORM_URLENCODED);
        Response response = client.target(GOOGLE_TOKEN_REQUEST_URL)
                .request().header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED)
                .post(entity);
        if (response.getStatus() != 200) {
            throw new BadRequestException("Request response status was not 200 OK");
        }
        token = response.readEntity(Token.class);
        token.setTokenAcquireTime(currentTimeMillis());
    }

    private void refreshToken() {
        Entity<Form> entity = Entity.entity(new Form()
                .param("client_id", controller.getClientIdBoxText())
                .param("client_secret", controller.getClientSecretBoxText())
                .param("grant_type", "authorization_code")
                .param("refresh_token", token.getRefreshToken()), MediaType.APPLICATION_FORM_URLENCODED);
        Response response = client.target(GOOGLE_TOKEN_REQUEST_URL)
                .request().header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED)
                .post(entity);

        if (response.getStatus() != 200) {
            throw new BadRequestException("Request response status was not 200 OK");
        }

        Token newToken = response.readEntity(Token.class);
        token.setTokenAcquireTime(currentTimeMillis())
                .setAccessToken(newToken.getAccessToken())
                .setExpiresIn(newToken.getExpiresIn())
                .setTokenType(newToken.getTokenType());
    }

    @Override
    public PlaylistResponse getPlaylist(PlaylistRequest request) {
        if (isNotEmpty(request.getAuthorizationCode())) {
            getToken(request.getAuthorizationCode());
        }

        WebTarget webTarget = client.target(YOUTUBE_BASE_API_URI + "/playlistItems")
                .queryParam("access_token", token.getAccessToken())
                .queryParam("playlistId", request.getId())
                .queryParam("part", "id,snippet")
                .queryParam("maxResults", 50);

        String nextPageToken = request.getNextPageToken();
        if (isNotEmpty(nextPageToken)) {
            webTarget = webTarget.queryParam("pageToken", nextPageToken);
        }

        Response response = webTarget.request().get();
        if (response.getStatus() == 200) {
            PlaylistItemsResponse playlistItemsResponse = response.readEntity(PlaylistItemsResponse.class);
            List<PlaylistItem> items = playlistItemsResponse.getItems();
            List<Song> songs = new ArrayList<>(items.size());
            for (PlaylistItem playlistItem : items) {
                PlaylistItem.Snippet snippet = playlistItem.getSnippet();
                try {
                    songs.add(new Song(snippet.getTitle(), snippet.getThumbnailUrl(), new URL(YOUTUBE_BASE_URI + snippet.getVideoId())));
                } catch (MalformedURLException e) {
                    LOG.warn("Malformed URL encountered during page processing, skipping.");
                }
            }
            return new PlaylistResponse(songs, playlistItemsResponse.getNextPageToken());
        }
        throw new BadRequestException("Request response status was not 200");
    }

    //TODO: Implementation of authorization need check, potentially impractical due to need of auth to make auth check :D
    @Override
    public boolean requiresAuthorization(String playlist) {
        WebTarget webTarget = client.target(YOUTUBE_BASE_API_URI + "/playlists")
                .queryParam("access_token", token.getAccessToken())
                .queryParam("playlistId", playlist)
                .queryParam("part", "status")
                .queryParam("maxResults", 1);
        return true;
    }
}