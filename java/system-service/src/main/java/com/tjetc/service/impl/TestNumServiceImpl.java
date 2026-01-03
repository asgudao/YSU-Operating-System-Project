package com.tjetc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tjetc.common.JsonResult;
import com.tjetc.dao.TestNumMapper;
import com.tjetc.entity.TestNum;
import com.tjetc.service.TestNumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class TestNumServiceImpl implements TestNumService {
    @Autowired
    TestNumMapper testNumMapper;
    
    @Override
    @Transactional
    public JsonResult add(TestNum testNum){
        testNumMapper.insert(testNum);
        return JsonResult.success("新增数据成功");
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


    @Override
    public JsonResult findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TestNum> testNums = testNumMapper.selectAll();
        PageInfo<TestNum> testNumPageInfo = new PageInfo<>(testNums);
        return JsonResult.success(testNumPageInfo);
    }


    @Override
    public JsonResult<TestNum> findAll(){
        List<TestNum> testNumList=testNumMapper.selectAll();
        return JsonResult.success(testNumList);
    }


    @Override
    public JsonResult deleteById(Integer id){
        if(id == null||id<=0){
            return JsonResult.fail("id为不得为空或者小于0");
        }
        int affectRows = testNumMapper.deleteById(id);
        if(affectRows > 0){
            return JsonResult.success("删除数据成功");
        }else{
            return JsonResult.fail("删除数据失败");
        }
    }
}
