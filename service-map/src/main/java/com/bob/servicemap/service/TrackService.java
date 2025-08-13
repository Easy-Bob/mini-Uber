package com.bob.servicemap.service;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.TrackResponse;
import com.bob.servicemap.remote.TerminalClient;
import com.bob.servicemap.remote.TrackClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by Sun on 2025/8/13.
 * Description:
 */
@Service
public class TrackService {

    @Autowired
    private TrackClient trackClient;

    public ResponseResult<TrackResponse> add(String tid){
        return trackClient.add(tid);
    }


}
