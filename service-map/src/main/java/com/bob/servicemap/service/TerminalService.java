package com.bob.servicemap.service;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.TerminalResponse;
import com.bob.internalcommon.constant.response.TrsearchResponse;
import com.bob.servicemap.remote.TerminalClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Sun on 2025/8/13.
 * Description:
 */
@Service
public class TerminalService {

    @Autowired
    private TerminalClient terminalClient;

    public ResponseResult<TerminalResponse> add(String name, String desc){
        return terminalClient.add(name, desc);
    }

    public ResponseResult aroundSearch(String center, Integer radius){
        return terminalClient.aroundSearch(center, radius);
    }

    public ResponseResult<TrsearchResponse> trsearch(String tid, Long starttime, Long endtime) {
        return terminalClient.trsearch(tid,starttime,endtime);
    }
}
