package yousync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yousync.sources.MusicSource;
import yousync.sources.YouTubeSource;

@Service
public class SourceResolverService {
    @Autowired
    private YouTubeSource youTubeSource;

    public MusicSource resolveResource(final String value){
        //TODO: Feature support for multiple resource resolvers
        return youTubeSource;
    }
}
