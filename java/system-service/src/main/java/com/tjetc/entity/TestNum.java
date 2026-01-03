package com.tjetc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @TableField("use_TLB")
    private Integer useTLB;

    @TableField("TLB_num")
    @JsonProperty("TLBNum")
    private Integer TLBNum;

    private Integer visitMemory;

    @TableField("visit_TLB")
    private Integer visitTLB;
    @TableField("handle_losepage")
    private Integer handleLosepage;

    private String inputNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
