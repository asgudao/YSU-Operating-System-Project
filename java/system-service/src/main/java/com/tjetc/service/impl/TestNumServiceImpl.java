package com.tjetc.service.impl;

import com.tjetc.common.JsonResult;
import com.tjetc.dao.TestNumMapper;
import com.tjetc.entity.TestNum;
import com.tjetc.service.TestNumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TestNumServiceImpl implements TestNumService {
    @Autowired
    TestNumMapper testNumMapper;
    
    @Override
    @Transactional
    public JsonResult add(TestNum testNum){
        try {
            testNumMapper.insert(testNum);
            return JsonResult.success("新增数据成功");
        }catch (Exception e){
            throw new RuntimeException("e");
        }
    }
    
    @Override
    public JsonResult findById(Integer id){
        if (id == null || id <= 0) {
            return JsonResult.fail("用户id不为空或者小于0");
        }
        TestNum testNum = testNumMapper.selectById(id);
        if (testNum == null) {
            return JsonResult.fail("此数据不存在");
        }
        return JsonResult.success(testNum);
    }
}
