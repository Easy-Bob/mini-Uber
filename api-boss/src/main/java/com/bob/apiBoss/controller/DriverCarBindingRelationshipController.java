package com.bob.apiBoss.controller;

import com.bob.apiBoss.remote.DriverUserCarClient;
import com.bob.internalcommon.constant.dto.DriverCarBindingRelationship;
import com.bob.internalcommon.constant.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@RestController
@RequestMapping("/car")
public class DriverCarBindingRelationshipController {

    @Autowired
    private DriverUserCarClient client;

    @PostMapping("/bind")
    public ResponseResult bind(@RequestBody DriverCarBindingRelationship o){
        return client.driverCarBind(o);
    }

    @PostMapping("/unbind")
    public ResponseResult unbind(@RequestBody DriverCarBindingRelationship o){
        return client.driverCarUnbind(o);
    }
}
