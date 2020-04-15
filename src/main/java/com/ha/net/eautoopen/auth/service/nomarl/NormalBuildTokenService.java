package com.ha.net.eautoopen.auth.service.nomarl;

import com.ha.net.eautoopen.dto.ConsumerCache;
import com.ha.net.eautoopen.dto.Signature;

public interface NormalBuildTokenService {

    public Signature build(String consumer, ConsumerCache consumerCache)throws Exception;

    public Signature firstBuild(String consumer, ConsumerCache ConsumerCache)throws Exception;

}
