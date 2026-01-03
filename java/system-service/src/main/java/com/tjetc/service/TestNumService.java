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

    /**
     * 根据用户名模糊分页查询用户信息
     *
     * @param pageNo    页面
     * @param pageSize  每页数量
     * @return
     */
    JsonResult findPage(Integer pageNo, Integer pageSize);

    /**
     * 根据id删除
      * @param id
     * @return
     */
    JsonResult deleteById(Integer id);

}
