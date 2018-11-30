package yousync.domain.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistResponse {
    private String status;

    @JsonProperty("status")
    private void unpackStatus(Map<String, Object> status) {
        this.status = (String) status.get("privacyStatus");
    }
}