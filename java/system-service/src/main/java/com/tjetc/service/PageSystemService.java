package com.tjetc.service;

import com.tjetc.common.JsonResult;
import com.tjetc.entity.TestNum;

public interface PageSystemService {

    JsonResult inputProcess(String input);


    JsonResult start(TestNum testNum);

    JsonResult getChange();

    JsonResult test(TestNum testNum);
}
