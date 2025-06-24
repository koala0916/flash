package com.powernode.map.service;

import com.powernode.model.form.map.CalculateDrivingLineForm;
import com.powernode.model.vo.map.DrivingLineVo;

public interface MapService {

    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm);
}
