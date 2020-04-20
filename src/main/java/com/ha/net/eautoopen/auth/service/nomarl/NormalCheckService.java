package com.ha.net.eautoopen.auth.service.nomarl;

import com.ha.net.eautoopen.dto.ConsumerCache;

public interface NormalCheckService {

    public ConsumerCache check(String consumer)throws Exception;

}
