package com.tjetc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tjetc.entity.Page;
import com.tjetc.entity.TestNum;

import java.util.List;

public interface TestNumMapper extends BaseMapper<TestNum> {
    /**
     * 根据id进行查询
     * @param id
     * @return
     */
    TestNum selectById(Integer id);

    /**
     * 查询所有的信息
     * @return
     */
    List<TestNum> selectAll();
}
