package com.bob.internalcommon.constant.request;

import com.bob.internalcommon.constant.dto.PointDTO;
import lombok.Data;

/**
 * Created by Sun on 2025/8/13.
 * Description:
 */
@Data
public class PointRequest {
    private String tid;
    private String trid;
    private PointDTO[] points;
}
