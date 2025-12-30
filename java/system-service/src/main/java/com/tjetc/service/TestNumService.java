package com.tjetc.service;

import com.tjetc.common.JsonResult;
import com.tjetc.entity.TestNum;

public interface TestNumService {
    /**
     * 新增实验数据
     * @param testNum
     * @return
     */
    JsonResult add(TestNum testNum);

    /**
     * 查询所有数据
     *
     * @return
     */
    JsonResult<TestNum> findAll();

    /**
     * 根据id查询用户信息
     *
     * @param id
     * @return
     */
    JsonResult findById(Integer id);

}
