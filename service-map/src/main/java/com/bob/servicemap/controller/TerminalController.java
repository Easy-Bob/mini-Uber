package com.bob.servicemap.controller;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.TerminalResponse;
import com.bob.servicemap.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/13.
 * Description:
 */
@RestController
@RequestMapping("/terminal")
public class TerminalController {

    @Autowired
    private TerminalService terminalService;

    @PostMapping("/add")
    public ResponseResult<TerminalResponse> add(String name){
        return terminalService.add(name);
    }

}
