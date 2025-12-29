package com.tjetc.controller;


import com.tjetc.common.JsonResult;
import com.tjetc.entity.Page;
import com.tjetc.entity.TestNum;
import com.tjetc.service.PageService;
import com.tjetc.service.TestNumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UseController {
    @Autowired
    private PageService pageService;
    @Autowired
    private TestNumService testNumService;

    @RequestMapping("page/add")
    public JsonResult add(Page page){
        return pageService.add(page);
    }

    @RequestMapping("testNum/add")
    public JsonResult add(TestNum testNum){
        return testNumService.add(testNum);
    }

    @RequestMapping("page/delete/{id}")
    public JsonResult delete(@PathVariable("id") Integer id){
        return pageService.deleteById(id);
    }

    @RequestMapping("page/select/all")
    public JsonResult<Page> selectAll(){
        return pageService.findAll();
    }

    @RequestMapping("page/select/{id}")
    public JsonResult selectByPageId(@PathVariable("id") Integer id){
        return pageService.findById(id);
    }

    @RequestMapping("testNum/select/{id}")
    public JsonResult selectByTestNumId(@PathVariable("id") Integer id){
        return testNumService.findById(id);
    }
}

