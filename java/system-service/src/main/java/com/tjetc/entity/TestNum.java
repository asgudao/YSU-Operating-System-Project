package com.tjetc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("test_num")
public class TestNum {
    private Integer id;
    private Integer pageNum;
    private Integer useTLB;
    private Integer TLBNum;
    private Integer visitMemory;
    private Integer visitTLB;
    private Integer handleLosepage;
    private Integer visitNum;
    private Integer visitBegin;
    private Integer visitEnd;
    private String inputNum;
}
