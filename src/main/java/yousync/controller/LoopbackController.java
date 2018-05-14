package yousync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import yousync.domain.PlaylistRequest;
import yousync.services.CoreService;
import yousync.sources.YouTubeSource;
import yousync.ui.YouTubeTabController;

@Controller
public class LoopbackController {
    @Autowired
    private CoreService coreService;
    @Autowired
    private YouTubeSource youTubeSource;
    @Autowired
    private YouTubeTabController controller;

    @RequestMapping(value = "/loopback", method = RequestMethod.GET)
    void getAuthentication(@RequestParam("code") String code) {
        //TODO: Can be fixed by defining custom ViewResolver. javax.servlet.ServletException: Circular view path []: would dispatch back to the current handler URL
        controller.closeWebAuthenticationWindow();
        coreService.loadSongs(youTubeSource, new PlaylistRequest(controller.getPlaylistIdBoxText(), null, code));
    }
}