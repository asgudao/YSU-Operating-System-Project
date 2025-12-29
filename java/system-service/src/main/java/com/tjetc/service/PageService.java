package com.tjetc.service;

import com.tjetc.common.JsonResult;
import com.tjetc.entity.Page;

public interface PageService {
    /**
     * 查询所有数据
     * @return
     */
    JsonResult<Page> findAll();

    /**
     * 新增实验数据
     * @param page
     * @return
     */
    JsonResult add(Page page);


    /**
     * 根据id查询数据信息
     *
     * @param id
     * @return
     */
    JsonResult findById(Integer id);


    /**
     * 根据id删除
     * @param id
     * @return
     */
    JsonResult deleteById(Integer id);


}
