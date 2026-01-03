package com.tjetc.service.impl;

import com.tjetc.common.JsonResult;
import com.tjetc.dao.TestNumMapper;
import com.tjetc.entity.Change;
import com.tjetc.entity.Page;
import com.tjetc.entity.TestNum;
import com.tjetc.service.PageService;
import com.tjetc.service.PageSystemService;
import com.tjetc.service.TestNumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class PageSystemImpl implements PageSystemService {
    @Autowired
    private TestNumService testNumService;
    @Autowired
    private PageService pageService;

    //变量定义
    private List<String> FIFO_TLB = new ArrayList<>();
    private List<String> FIFO_PageTable = new ArrayList<>();
    private Integer FIFO_Time=0;
    private Integer FIFO_Losepage=0;
    private List<String> LRU_TLB = new ArrayList<>();
    private List<String> LRU_PageTable = new ArrayList<>();
    private Integer LRU_Time=0;
    private Integer LRU_Losepage=0;
    private List<String> LFU_TLB = new ArrayList<>();
    private List<String> LFU_PageTable = new ArrayList<>();
    private Integer LFU_Time=0;
    private Integer LFU_Losepage=0;

    private TestNum testNum = new TestNum();
    private Page page=new Page();
    private Change change=new Change();
    List<String> input_num=new ArrayList<>();
    private String[][] FIFO_TableChange;
    private String[][] LFU_TableChange;
    private String[][] LRU_TableChange;

    private String[][] FIFO_TLBChange;
    private String[][] LFU_TLBChange;
    private String[][] LRU_TLBChange;

    private Integer ifSuccess=0;

    //主函数
    @Override
    public JsonResult start(TestNum testNum){
        this.testNum = testNum;
        inputProcess(testNum.getInputNum());//设置testNum的input_num属性
        FIFO_TableChange=new String[testNum.getPageNum()][input_num.size()];
        LFU_TableChange=new String[testNum.getPageNum()][input_num.size()];
        LRU_TableChange=new String[testNum.getPageNum()][input_num.size()];
        FIFO_TLBChange=new String[testNum.getTLBNum()][input_num.size()];
        LFU_TLBChange=new String[testNum.getTLBNum()][input_num.size()];
        LRU_TLBChange=new String[testNum.getTLBNum()][input_num.size()];
        FIFO();
        LRU();
        LFU();
        change.setFIFO_TableChange(FIFO_TableChange);
        change.setLFU_TableChange(LFU_TableChange);
        change.setLRU_TableChange(LRU_TableChange);
        change.setFIFO_TLBChange(FIFO_TLBChange);
        change.setLFU_TLBChange(LFU_TLBChange);
        change.setLRU_TLBChange(LRU_TLBChange);
        pageService.add(page);
        testNumService.add(testNum);
        ifSuccess=1;
        return JsonResult.success("运行成功",testNum);
    }

    @Override
    public JsonResult getChange(){
        if (ifSuccess==1){
            return JsonResult.success("运行成功",change);
        }
        return JsonResult.fail("运行失败");
    }


    //输入处理,设置了input_num属性
    @Override
    public JsonResult inputProcess(String input){
        List<String> processInput = Arrays.asList(input.trim().split("[,;\\s，。‘’'\"“”、]+"));
        for (String str : processInput) {
            this.input_num.add(str);
        }
        return JsonResult.success("输入转化成功");
    }

    //用于给tablechange赋值
    public JsonResult record(String[][] TableChange,List<String> list,Integer i){
        for(int j = 0;j<input_num.size();j++){//行
            TableChange[i][j]=list.get(j);
        }
        return JsonResult.success("success");
    }

    public JsonResult FIFO(){
        List <String>list_page = new ArrayList<>();
        List<String>list_TLB = new ArrayList<>();
        if(testNum.getUseTLB()==0){//没有快表
            for(int i=0;i<input_num.size();i++){
                if(!FIFO_PageTable.contains(input_num.get(i))){//页表未查询到，产生缺页中断
                    FIFO_Time+=testNum.getVisitMemory();//加页表查询时间
                    if(FIFO_PageTable.size()==testNum.getPageNum()){//页表满了
                        FIFO_PageTable.remove(list_page.get(0));//去掉页表中最先进入的值
                        list_page.remove(0);//去掉队列中最先进入的值
                    }
                    list_page.add(input_num.get(i));//加入一个新值
                    FIFO_PageTable.add(input_num.get(i));//给页表中加入此数据
                    FIFO_Time += testNum.getHandleLosepage();//加处理缺页中断情况的时间
                    FIFO_Losepage++;//加出现缺页中断情况的次数
                    i--;//让下次接着查询此值
                }
                else{
                    FIFO_Time+=testNum.getVisitMemory();//正常查询页表
                }
                record(FIFO_TableChange,FIFO_PageTable,i);
                FIFO_Time+=testNum.getVisitMemory();//获取数据查询页表
            }
        }
        else if (testNum.getUseTLB()==1){
            for(int i=0;i<input_num.size();i++){
                if(!FIFO_TLB.contains(input_num.get(i))){//tlb未查询到
                    FIFO_Time+=testNum.getVisitTLB();
                    if(!FIFO_PageTable.contains(input_num.get(i))){//页表未查询到，产生缺页中断
                        FIFO_Time+=testNum.getVisitMemory();//查询页表时间
                        if(FIFO_TLB.size()==testNum.getTLBNum()){
                            FIFO_TLB.remove(list_TLB.get(0));//去掉快表中最先进入的那个值
                            list_TLB.remove(0);//去掉最先进入的值
                        }
                        if(FIFO_PageTable.size()==testNum.getPageNum()){//页表满了
                            FIFO_PageTable.remove(list_page.get(0));//去掉页表中最先进入的那个值
                            list_page.remove(0);//去掉最先进入的值
                        }
                        list_page.add(input_num.get(i));//给队列中加入数据
                        list_TLB.add(input_num.get(i));//给快表队列中加入数据
                        FIFO_TLB.add(input_num.get(i));//给快表中加入此数据
                        FIFO_PageTable.add(input_num.get(i));//给页表中加入此数据
                        FIFO_Time += testNum.getHandleLosepage();//加处理缺页中断的情况
                        FIFO_Losepage++;//加出现缺页中断情况的次数
                        i--;//让下次接着查询
                    }
                    else{//页表查询到
                        FIFO_Time+=testNum.getVisitMemory();
                    }
                }
                else{//tlb查询到
                    FIFO_Time+=testNum.getVisitTLB();
                }
                FIFO_Time+=testNum.getVisitMemory();
                record(FIFO_TableChange,FIFO_PageTable,i);
                record(FIFO_TLBChange,FIFO_TLB,i);
            }
        }
        page.setFIFOTime(FIFO_Time);
        page.setFIFOLosepage(FIFO_Losepage);
        return JsonResult.success("FIFO成功运行");
    }


    public JsonResult LRU(){
        List <String> list_page = new ArrayList<>();
        List <String>list_TLB = new ArrayList<>();
        if(testNum.getUseTLB()==0){//没有快表
            for(int i=0;i<input_num.size();i++){
                if(!LRU_PageTable.contains(input_num.get(i))){//页表未查询到，产生缺页中断
                    LRU_Time+=testNum.getVisitMemory();
                    if(LRU_PageTable.size()==testNum.getPageNum()){//页表满了
                        LRU_PageTable.remove(list_page.get(0));//去掉页表中最久未使用的值
                        list_page.remove(0);//去掉最久未使用的值
                    }
                    list_page.add(input_num.get(i));//加入一个新值
                    LRU_PageTable.add(input_num.get(i));//给页表中加入此数据
                    LRU_Time += testNum.getHandleLosepage();//加处理缺页中断的情况
                    LRU_Losepage++;//加出现缺页中断情况的次数
                    i--;//让下次接着查询此值
                }
                else{
                    list_page.remove(input_num.get(i));
                    list_page.add(input_num.get(i));//新访问过，调整顺序
                    LRU_Time+=testNum.getVisitMemory();//正常查询页表
                }
                record(LRU_TableChange,LRU_PageTable,i);
                LRU_Time+=testNum.getVisitMemory();
            }
        }
        else if (testNum.getUseTLB()==1){
            for(int i=0;i<input_num.size();i++){
                if(!LRU_TLB.contains(input_num.get(i))){//tlb未查询到
                    if(!LRU_PageTable.contains(input_num.get(i))){//页表未查询到，产生缺页中断
                        if (LRU_TLB.size()==testNum.getTLBNum()) {
                            LRU_TLB.remove(list_page.get(0));//去掉快表中最久未使用的那个值
                            list_TLB.remove(0);
                        }
                        if(LRU_PageTable.size()==testNum.getPageNum()){//页表满了
                            LRU_PageTable.remove(list_page.get(0));//去掉页表中最久未使用的那个值
                            list_page.remove(0);//去掉最久未使用的值
                        }
                        list_TLB.add(input_num.get(i));
                        list_page.add(input_num.get(i));//加入一个新值
                        LRU_TLB.add(input_num.get(i));//给快表中加入此数据
                        LRU_PageTable.add(input_num.get(i));//给页表中加入此数据
                        LRU_Time += testNum.getHandleLosepage();//加处理缺页中断的情况
                        LRU_Losepage++;//加出现缺页中断情况的次数
                        i--;//让下次接着查询
                    }
                    else{
                        list_page.remove(input_num.get(i));
                        list_page.add(input_num.get(i));
                        LRU_Time+=testNum.getVisitMemory();
                    }
                }
                else{//tlb查询到
                    LRU_Time+=testNum.getVisitTLB();
                    list_TLB.remove(input_num.get(i));
                    list_TLB.add(input_num.get(i));
                    list_page.remove(input_num.get(i));
                    list_page.add(input_num.get(i));
                }
                LRU_Time+=testNum.getVisitMemory();
                record(LRU_TableChange,LRU_PageTable,i);
                record (LRU_TLBChange,LRU_TLB,i);
            }
        }
        page.setLRUTime(LRU_Time);
        page.setLRULosepage(LRU_Losepage);
        return JsonResult.success("LRU成功运行");
    }


    public JsonResult LFU(){
        Map <String,Integer> list_page = new LinkedHashMap<>();
        Map <String,Integer> list_TLB = new LinkedHashMap<>();
        if(testNum.getUseTLB()==0){//没有快表
            for(int i=0;i<input_num.size();i++){
                if(!LFU_PageTable.contains(input_num.get(i))){//页表未查询到，产生缺页中断
                    LFU_Time+=testNum.getVisitMemory();
                    if(LFU_PageTable.size()==testNum.getPageNum()){//页表满了
                        String delete_num=list_TLB.keySet().iterator().next();
                        LFU_PageTable.remove(delete_num);//去掉页表中最久未使用的值
                        list_page.remove(delete_num);//去掉最久未使用的值
                    }
                    list_page.put(input_num.get(i),1);//加入一个新值
                    LFU_PageTable.add(input_num.get(i));//给页表中加入此数据
                    LFU_Time += testNum.getHandleLosepage();//加处理缺页中断的情况
                    LFU_Losepage++;//加出现缺页中断情况的次数
                    i--;//让下次接着查询此值
                }
                else{
                    list_page.merge(input_num.get(i),1,Integer::sum);
                    LFU_Time+=testNum.getVisitMemory();//正常查询页表
                }
                record(LFU_TableChange,LFU_PageTable,i);
                LFU_Time+=testNum.getVisitMemory();
            }
        }
        else if (testNum.getUseTLB()==1){
            for(int i=0;i<input_num.size();i++){
                if(!LFU_TLB.contains(input_num.get(i))){//tlb未查询到
                    if(!LFU_PageTable.contains(input_num.get(i))){//页表未查询到，产生缺页中断
                        if(LFU_TLB.size()==testNum.getTLBNum()){//快表满了
                            String delete_num=list_TLB.keySet().iterator().next();
                            LFU_TLB.remove(delete_num);//去掉快表中最久未使用的那个值
                            list_TLB.remove(delete_num);
                        }
                        if(LFU_PageTable.size()==testNum.getPageNum()){//页表满了
                            String delete_num=list_TLB.keySet().iterator().next();
                            LFU_PageTable.remove(delete_num);//去掉页表中最久未使用的那个值
                            list_page.remove(delete_num);//去掉最久未使用的值
                        }
                        list_page.put(input_num.get(i),1);//加入一个新值
                        LFU_TLB.add(input_num.get(i));//给快表中加入此数据
                        LFU_PageTable.add(input_num.get(i));//给页表中加入此数据
                        LFU_Time += testNum.getHandleLosepage();//加处理缺页中断的情况
                        LFU_Losepage++;//加出现缺页中断情况的次数
                        i--;//让下次接着查询
                    }
                    else{
                        list_page.merge(input_num.get(i),1,Integer::sum);
                        LFU_Time+=testNum.getVisitMemory();
                    }
                }
                else{//tlb查询到
                    LFU_Time+=testNum.getVisitTLB();
                    list_TLB.merge(input_num.get(i),1,Integer::sum);
                }
                LFU_Time+=testNum.getVisitMemory();
                record(LFU_TableChange,LFU_PageTable,i);
                record (LFU_TLBChange,LFU_TLB,i);
            }
        }
        page.setLFUTime(LFU_Time);
        page.setLFULosepage(LFU_Losepage);
        return JsonResult.success("LFU成功运行");
    }


}
