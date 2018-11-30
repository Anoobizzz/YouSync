package yousync.domain;

public class PlaylistRequest {
    private String id;
    private String nextPageToken;

    public PlaylistRequest(String id) {
        this.id = id;
    }

    public PlaylistRequest(String id, String nextPageToken) {
        this.id = id;
        this.nextPageToken = nextPageToken;
    }

    public String getId() {
        return id;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
