package com.tjetc.entity;


import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableField("FIFO_losepage")
    private Integer FIFOLosepage;
    @TableField("LRU_losepage")
    private Integer LRULosepage;
    @TableField("LFU_losepage")
    private Integer LFULosepage;
    @TableField("FIFO_time")
    private Integer FIFOTime;
    @TableField("LRU_time")
    private Integer LRUTime;
    @TableField("LFU_time")
    private Integer LFUTime;


}
