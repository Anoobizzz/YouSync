package yousync.domain.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistItemsResponse {
    private List<PlaylistItem> items;
    private String nextPageToken;
    private String prevPageToken;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaylistItem {

        private Snippet snippet;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public class Snippet {
            private String title;
            private String videoId;
            private String thumbnailUrl;

            @SuppressWarnings("unchecked")
            @JsonProperty("resourceId")
            private void unpackResourceId(Map<String, Object> resource) {
                this.videoId = (String) resource.get("videoId");
            }

            @SuppressWarnings("unchecked")
            @JsonProperty("thumbnails")
            private void unpackThumbnailUrl(Map<String, Object> resource) {
                this.thumbnailUrl = ((Map<String, String>) resource.get("medium")).get("url");
            }

            public String getTitle() {
                return title;
            }

            public String getVideoId() {
                return this.videoId;
            }

            public String getThumbnailUrl() {
                return thumbnailUrl;
            }
        }

        public Snippet getSnippet() {
            return snippet;
        }
    }

    public List<PlaylistItem> getItems() {
        return items;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
