package com.ha.net.eautoopen.auth.service.nomarl;

import com.ha.net.eautoopen.dto.ConsumerCache;

public interface NormalCheckService {

    public Boolean check(String consumer,ConsumerCache consumerCache)throws Exception;

}
