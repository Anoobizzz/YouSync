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
import yousync.ui.AlertController;
import yousync.ui.MainController;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.CompletableFuture.runAsync;
import static javafx.application.Platform.runLater;
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

    @Autowired
    private MainController mainController;
    @Autowired
    private Client client;

    private Token token = new Token();
    private String authorizationCode = null;

    //TODO: Implement check for authorization requirement
    @Override
    public boolean requiresAuthorization(String playlist) {
        //Playlist GET https://www.youtube.com/playlist?list=id and look for alerts array
        //Videos?
        return true;
    }

    @Override
    public boolean isAuthorized() {
        return isNotEmpty(authorizationCode);
    }

    @Override
    public CompletableFuture<Void> authorize(final String clientId) {
        final String page = client.target(GOOGLE_LOOPBACK_OAUTH_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", "http://127.0.0.1:8080/loopback")
                .queryParam("response_type", "code")
                .queryParam("scope", "https://www.googleapis.com/auth/youtube.force-ssl")
                .request().get().readEntity(String.class);
        runLater(() -> mainController.displayWebAuthenticationWindow(page));
        return runAsync(() -> {
            //TODO: Redo the socket response await process
            try (final ServerSocket loopbackSocket = new ServerSocket(8080)) {
                final BufferedReader in = new BufferedReader(new InputStreamReader(loopbackSocket.accept().getInputStream()));
                authorizationCode = in.lines().filter(s -> s.contains("code=")).findFirst()
                        .orElseThrow(() -> new BadRequestException("Failed to retrieve authorization code"))
                        .replaceAll(".*code=([^\\s&]*).*", "$1");
            } catch (IOException e) {
                final String errorMessage = e.getMessage();
                LOG.error(errorMessage);
                AlertController.showErrorWindow(errorMessage);
            }
            runLater(() -> mainController.hideWebAuthenticationWindow());
        });
    }

    private String getToken(final String clientId, final String clientSecret) {
        if (isNotEmpty(token.getRefreshToken())) {
            if (token.getExpirationTimeMillis() < currentTimeMillis()) {
                refreshAccessToken(clientSecret, clientId);
            }
            return token.getAccessToken();
        }
        saveNewAccessToken(clientId, clientSecret);
        return token.getAccessToken();
    }

    private void saveNewAccessToken(final String clientId, final String clientSecret) {
        fetchAndSaveToken(Entity.entity(new Form()
                .param("client_id", clientId)
                .param("client_secret", clientSecret)
                .param("redirect_uri", "http://127.0.0.1:8080/loopback")
                .param("grant_type", "authorization_code")
                .param("code", authorizationCode), MediaType.APPLICATION_FORM_URLENCODED));
    }

    private void refreshAccessToken(final String clientSecret, final String clientId) {
        fetchAndSaveToken(Entity.entity(new Form()
                .param("client_id", clientId)
                .param("client_secret", clientSecret)
                .param("grant_type", "authorization_code")
                .param("refresh_token", token.getRefreshToken()), MediaType.APPLICATION_FORM_URLENCODED));
    }

    private void fetchAndSaveToken(final Entity<Form> entity) {
        final Response response = client.target(GOOGLE_TOKEN_REQUEST_URL)
                .request().header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED)
                .post(entity);

        final int status = response.getStatus();
        if (status != 200) {
            throw new BadRequestException("Failed to fetch access token with status: " + status);
        }
        token = response.readEntity(Token.class).setTokenAcquireTime(currentTimeMillis());
    }

    @Override
    public PlaylistResponse getPlaylist(final PlaylistRequest request) {
        WebTarget webTarget = client.target(YOUTUBE_BASE_API_URI + "/playlistItems")
                .queryParam("access_token", getToken(request.getClientId(), request.getClientSecret()))
                .queryParam("playlistId", request.getId())
                .queryParam("part", "id,snippet")
                .queryParam("maxResults", 50);

        final String nextPageToken = request.getNextPageToken();
        if (isNotEmpty(nextPageToken)) {
            webTarget = webTarget.queryParam("pageToken", nextPageToken);
        }

        final Response response = webTarget.request().get();
        final int status = response.getStatus();
        if (status == 200) {
            PlaylistItemsResponse playlistItemsResponse = response.readEntity(PlaylistItemsResponse.class);
            final List<PlaylistItem> items = playlistItemsResponse.getItems();
            final List<Song> songs = new ArrayList<>(items.size());
            for (final PlaylistItem playlistItem : items) {
                final PlaylistItem.Snippet snippet = playlistItem.getSnippet();
                try {
                    songs.add(new Song(snippet.getTitle(), snippet.getThumbnailUrl(), new URL(YOUTUBE_BASE_URI + snippet.getVideoId())));
                } catch (MalformedURLException e) {
                    LOG.warn("Malformed URL encountered during page processing, skipping.");
                }
            }
            //TODO: Not all pages of playlist consumed, check whats wrong
            return new PlaylistResponse(songs, playlistItemsResponse.getNextPageToken());
        }
        throw new BadRequestException("Failed to get playlist from YouTube with status: " + status);
    }
}