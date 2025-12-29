package com.tjetc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tjetc.entity.Page;
import com.tjetc.entity.TestNum;

public interface TestNumMapper extends BaseMapper<TestNum> {
    /**
     * 根据id进行查询
     * @param id
     * @return
     */
    TestNum selectById(Integer id);
}
