package com.tjetc.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Change {
    private String[][] FIFO_TableChange;
    private String[][] LFU_TableChange;
    private String[][] LRU_TableChange;
    private String[][] FIFO_TLBChange;
    private String[][] LFU_TLBChange;
    private String[][] LRU_TLBChange;
}
