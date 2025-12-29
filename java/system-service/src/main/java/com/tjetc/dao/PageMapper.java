package com.tjetc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tjetc.entity.Page;

import java.util.List;

public interface PageMapper extends BaseMapper<Page> {
    /**
     * 返回所有的数据值
     * @return
     */
    List<Page> selectAll();


    /**
     * 根据id进行查询
     * @param id
     * @return
     */
    Page selectById(Integer id);

}
