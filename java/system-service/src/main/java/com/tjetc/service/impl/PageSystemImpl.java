package com.tjetc.service.impl;

import com.tjetc.common.JsonResult;
import com.tjetc.dao.TestNumMapper;
import com.tjetc.entity.Page;
import com.tjetc.entity.TestNum;
import com.tjetc.service.PageService;
import com.tjetc.service.PageSystemService;
import com.tjetc.service.TestNumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PageSystemImpl implements PageSystemService {
    @Autowired
    private PageSystemService pageSystemService;
    @Autowired
    private TestNumMapper testNumMapper;

    private ConcurrentHashMap<String, String> All = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> FIFO_TLB = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> FIFO_PageTable = new ConcurrentHashMap<>();
    private Integer FIFO_Time=0;
    private Integer FIFO_Losepage=0;
    private ConcurrentHashMap<String, String> LRU_TLB = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> LRU_PageTable = new ConcurrentHashMap<>();
    private Integer LRU_Time=0;
    private Integer LRU_Losepage=0;
    private ConcurrentHashMap<String, String> LFU_TLB = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> LFU_PageTable = new ConcurrentHashMap<>();
    private Integer LFU_Time=0;
    private Integer LFU_Losepage=0;
    private ConcurrentHashMap<String, String> OPT_TLB = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> OPT_PageTable = new ConcurrentHashMap<>();
    private Integer OPT_Time=0;
    private Integer OPT_Losepage=0;
    private TestNum testNum = new TestNum();
    private Page page=new Page();
    List<String> input_num=new ArrayList<>();
    List<String> output_num=new ArrayList<>();
    @Autowired
    private TestNumService testNumService;
    @Autowired
    private PageService pageService;


    @Override
    public JsonResult start(TestNum testNum){
        this.testNum = testNum;
        inputProcess(testNum.getInputNum());
        FIFO();
        LRU();
        LFU();
        OPT();
        pageService.add(page);
        testNumService.add(testNum);
    }

    //设置了testNum的OutputNum属性，给All哈希表赋值,设置了input_num和output_num的值
    @Override
    public JsonResult inputProcess(String input){
        if(input==null||input.isBlank()){
            return JsonResult.fail("输入为空");
        }
        List<String> processInput = Arrays.asList(input.trim().split("[,;\\s，。‘’'\"“”、]+"));
        String output_str="";
        for (String str : processInput) {
            // 跳过空字符串（避免处理分割后可能出现的空元素）
            if (str.isEmpty()) {
                continue;
            }
            // 处理：最高位（第一个字符）移到最低位（末尾）
            if (str.length() == 1) {
                // 若字符串长度为1，移动后不变
                this.input_num.add(str);
                this.output_num.add(str);
                this.All.put(str,str);
                output_str += str+',';
            } else {
                // 截取第一个字符后的子串 + 第一个字符
                this.input_num.add(str);
                String processed = str.substring(1) + str.charAt(0);
                this.output_num.add(processed);
                this.All.put(str,processed);
                output_str += processed+',';
            }
            if (output_str.endsWith(",")) {
                output_str = output_str.substring(0, output_str.length()-1);
            }
            this.testNum.setOutputNum(output_str);
        }
        return JsonResult.success("输入转化成功");
    }


    public JsonResult FIFO(){
        Queue <String>queue_page = new LinkedList<>();
        if(testNum.getUseTLB()==0){//没有快表
            for(int i=0;i<input_num.size();i++){
                if(FIFO_PageTable.get(input_num.get(i))==null){//页表未查询到，产生缺页中断
                    i--;//让下次接着查询此值
                    if(FIFO_PageTable.size()==testNum.getPageNum()){//页表满了
                        FIFO_PageTable.remove(queue_page.peek());//去掉页表中最先进入的值
                        queue_page.poll();//去掉队列中最先进入的值
                    }
                    queue_page.offer(input_num.get(i));//加入一个新值
                    FIFO_PageTable.put(input_num.get(i), All.get(input_num.get(i)));//给页表中加入此数据
                    FIFO_Time += testNum.getHandleLosepage();//加处理缺页中断的情况
                    FIFO_Losepage++;//加出现缺页中断情况的次数
                }
                else{
                    FIFO_Time+=testNum.getVisitMemory();//正常查询页表
                }
            }
        }
        else if (testNum.getUseTLB()==1){
            for(int i=0;i<input_num.size();i++){
                if(FIFO_TLB.get(input_num.get(i))==null){//tlb未查询到
                    if(FIFO_PageTable.get(input_num.get(i))==null){//页表未查询到，产生缺页中断
                        i--;//让下次接着查询
                        if(FIFO_PageTable.size()==testNum.getPageNum()){//页表满了
                            FIFO_PageTable.remove(queue_page.peek());//去掉页表中最先进入的那个值
                            FIFO_TLB.remove(queue_page.peek());//去掉快表中最先进入的那个值
                            queue_page.poll();//去掉最先进入的值
                        }
                        queue_page.offer(input_num.get(i));
                        FIFO_TLB.put(input_num.get(i),All.get(input_num.get(i)));//给快表中加入此数据
                        FIFO_PageTable.put(input_num.get(i), All.get(input_num.get(i)));//给页表中加入此数据
                        FIFO_Time += testNum.getHandleLosepage();//加处理缺页中断的情况
                        FIFO_Losepage++;//加出现缺页中断情况的次数
                    }
                    else{
                        FIFO_Time+=testNum.getVisitMemory();
                    }
                }
                else{//tlb查询到
                    FIFO_Time+=testNum.getVisitTLB();
                }
            }
        }
        page.setFIFOTime(FIFO_Time);
        page.setFIFOLosepage(FIFO_Losepage);
        return JsonResult.success("FIFO成功运行");
    }


    public JsonResult LRU(){
        if(testNum.getUseTLB()==0){//没有快表
            for(int i=0;i<input_num.size();i++){
                if(LRU_PageTable.get(input_num.get(i))==null){//页表未查询到，产生缺页中断
                    i--;//让下次接着查询此值
                    if(LRU_PageTable.size()==testNum.getPageNum()){//页表满了
                        LRU_PageTable.remove(input_num.get(i-3));//去掉页表中最久未使用的值
                    }
                    LRU_PageTable.put(input_num.get(i), All.get(input_num.get(i)));//给页表中加入此数据
                    LRU_Time += testNum.getHandleLosepage();//加处理缺页中断的情况
                    LRU_Losepage++;//加出现缺页中断情况的次数
                }
                else{
                    LRU_Time+=testNum.getVisitMemory();//正常查询页表
                }
            }
        }
        else if (testNum.getUseTLB()==1){
            for(int i=0;i<input_num.size();i++){
                if(LRU_TLB.get(input_num.get(i))==null){//tlb未查询到
                    if(LRU_PageTable.get(input_num.get(i))==null){//页表未查询到，产生缺页中断
                        i--;//让下次接着查询
                        if(LRU_PageTable.size()==testNum.getPageNum()){//页表满了
                            LRU_PageTable.remove(input_num.get(i-3));//去掉页表中最久未使用的那个值
                            LRU_TLB.remove(input_num.get(i-3));//去掉快表中最久未使用的那个值
                        }
                        LRU_TLB.put(input_num.get(i),All.get(input_num.get(i)));//给快表中加入此数据
                        LRU_PageTable.put(input_num.get(i), All.get(input_num.get(i)));//给页表中加入此数据
                        LRU_Time += testNum.getHandleLosepage();//加处理缺页中断的情况
                        LRU_Losepage++;//加出现缺页中断情况的次数
                    }
                    else{
                        LRU_Time+=testNum.getVisitMemory();
                    }
                }
                else{//tlb查询到
                    LRU_Time+=testNum.getVisitTLB();
                }
            }
        }
        page.setLRUTime(LRU_Time);
        page.setLRULosepage(LRU_Losepage);
        return JsonResult.success("LRU成功运行");
    }



    public JsonResult LFU(){
        

    }


    public JsonResult OPT(){

    }

}
