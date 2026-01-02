package com.tjetc.controller;


import com.tjetc.common.JsonResult;
import com.tjetc.entity.Page;
import com.tjetc.entity.TestNum;
import com.tjetc.service.PageService;
import com.tjetc.service.PageSystemService;
import com.tjetc.service.TestNumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UseController {
    @Autowired
    private PageService pageService;
    @Autowired
    private TestNumService testNumService;
    @Autowired
    private PageSystemService pageSystemService;

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

    @RequestMapping("page")
    public JsonResult page(@RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return testNumService.findPage(pageNo, pageSize);
    }

    @RequestMapping("start")
    public JsonResult start(TestNum testNum){
        return pageSystemService.start(testNum);
    }
}

