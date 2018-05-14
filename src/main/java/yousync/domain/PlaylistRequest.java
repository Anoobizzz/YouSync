package yousync.domain;

public class PlaylistRequest {
    private String id;
    private String nextPageToken;
    private String authorizationCode;

    public PlaylistRequest(String id) {
        this.id = id;
    }

    public PlaylistRequest(String id, String nextPageToken) {
        this.id = id;
        this.nextPageToken = nextPageToken;
    }

    public PlaylistRequest(String id, String nextPageToken, String authorizationCode) {
        this.id = id;
        this.nextPageToken = nextPageToken;
        this.authorizationCode = authorizationCode;
    }

    public String getId() {
        return id;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }
}
