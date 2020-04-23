package com.ha.net.eautoopen.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class FallBackHandler {

    @ResponseBody
    @RequestMapping("/normalfallback")
    public String normalfallback(){
        log.info("**********服务降级啦**********");
        return "failed";
    }

}

