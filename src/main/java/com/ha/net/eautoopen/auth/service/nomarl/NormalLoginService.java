package com.ha.net.eautoopen.auth.service.nomarl;

import com.ha.net.eautoopen.dto.ConsumerCache;
import com.ha.net.eautoopen.dto.Signature;

public interface NormalLoginService {

    public Signature firstLogin(String consumer, String headInfos)throws Exception;

    public Signature login(String consumer, String headInfos, ConsumerCache consumerCache)throws Exception;

}
