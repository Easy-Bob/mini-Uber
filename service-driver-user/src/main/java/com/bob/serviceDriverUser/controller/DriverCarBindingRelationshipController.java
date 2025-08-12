package com.bob.serviceDriverUser.controller;

import com.bob.internalcommon.constant.dto.DriverCarBindingRelationship;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.serviceDriverUser.service.DriverCarBindingRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@RestController
@RequestMapping("/driver-car-binding-relationship")
public class DriverCarBindingRelationshipController {

    @Autowired
    private DriverCarBindingRelationshipService service;

    @PostMapping("/bind")
    public ResponseResult bind(@RequestBody DriverCarBindingRelationship o){
        return service.bind(o);
    }

    @PostMapping("/unbind")
    public ResponseResult unbind(@RequestBody DriverCarBindingRelationship o){
        return service.unbind(o);
    }
}
