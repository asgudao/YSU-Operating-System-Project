package com.tjetc.service.impl;


import com.tjetc.common.JsonResult;
import com.tjetc.dao.PageMapper;
import com.tjetc.entity.Page;
import com.tjetc.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PageServiceImpl implements PageService {
    @Autowired
    private PageMapper pageMapper;

    @Override
    public JsonResult<Page> findAll(){
        List<Page> list = pageMapper.selectAll();
        return JsonResult.success(list);
    }

    @Override
    @Transactional
    public JsonResult add(Page page){
        pageMapper.insert(page);
        return JsonResult.success("新增数据成功");
    }

    @Override
    public JsonResult findById(Integer id){
        if (id == null || id <= 0) {
            return JsonResult.fail("用户id不为空或者小于0");
        }
        Page page = pageMapper.selectById(id);
        if (page == null) {
            return JsonResult.fail("此数据不存在");
        }
        return JsonResult.success(page);
    }

    @Override
    public JsonResult deleteById(Integer id){
        if(id == null||id<=0){
            return JsonResult.fail("id为不得为空或者小于0");
        }
        int affectRows = pageMapper.deleteById(id);
        if(affectRows > 0){
            return JsonResult.success("删除数据成功");
        }else{
            return JsonResult.fail("删除数据失败");
        }
    }
}
