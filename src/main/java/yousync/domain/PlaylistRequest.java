package yousync.domain;

public class PlaylistRequest {
    private String id;
    private String nextPageToken;
    private String clientId;
    private String clientSecret;

    public PlaylistRequest(String id, String clientId, String clientSecret) {
        this.id = id;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getId() {
        return id;
    }

    public PlaylistRequest setId(String id) {
        this.id = id;
        return this;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public PlaylistRequest setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public PlaylistRequest setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public PlaylistRequest setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }
}
