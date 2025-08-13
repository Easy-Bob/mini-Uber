package com.bob.internalcommon.constant.request;

import com.bob.internalcommon.constant.dto.PointDTO;
import lombok.Data;

/**
 * Created by Sun on 2025/8/13.
 * Description:
 */
@Data
public class ApiDriverPointRequest {

    private Long carId;

    private PointDTO[] points;
}
