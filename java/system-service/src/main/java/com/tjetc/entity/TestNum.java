package com.tjetc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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
    private String inputNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
