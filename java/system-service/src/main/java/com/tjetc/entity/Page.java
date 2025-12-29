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
@TableName("page")
public class Page {
    private Integer id;
    private Integer tId;
    private Integer FIFOLosepage;
    private Integer LRULosepage;
    private Integer LFULosepage;
    private Integer OPTLosepage;
    private Integer FIFOTime;
    private Integer LRUTime;
    private Integer LFUTime;
    private Integer OPTime;

}
