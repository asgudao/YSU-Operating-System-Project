# 页面置换算法模拟系统（Paging System）
该项目基于 MySQL 数据库设计，用于存储页面置换算法（FIFO、LRU、LFU）的测试配置与执行结果，支撑操作系统中页面置换算法的模拟与性能分析。

## 项目简介
本数据库脚本定义了核心数据表，用于管理页面置换算法测试的参数配置、TLB（快表）相关参数、内存访问耗时、页面缺失处理耗时，以及不同算法的执行结果（如页面缺失次数、耗时等）。
- 支持 FIFO、LRU、LFU 三种经典页面置换算法
- 可配置 TLB 开关、TLB 容量、物理页框数等核心参数
- 记录页面访问序列、算法输出结果、各类耗时指标，便于算法性能对比分析

## 数据库设计
### 核心数据表
#### 1. test_num（测试配置表）
存储单次页面置换测试的核心配置与结果元数据，是整个系统的基础表。

| 字段名          | 类型         | 说明                                                                 |
|-----------------|--------------|----------------------------------------------------------------------|
| id              | int          | 主键，自增，唯一标识单次测试                                         |
| page_num        | int          | 物理页框数量（内存中可分配的页面帧数）                               |
| use_TLB         | int          | 是否启用TLB（1=启用，0=禁用）                                        |
| TLB_num         | int          | TLB（快表）容量                                                     |
| visit_memory    | int          | 访问内存的耗时（单位：微秒/纳秒，默认100）                           |
| visit_TLB       | int          | 访问TLB的耗时（单位：微秒/纳秒，默认10）                             |
| handle_losepage | int          | 处理页面缺失（缺页中断）的耗时（单位：微秒/纳秒，默认2000）           |
| input_num       | varchar(255) | 页面访问序列（如：1,3,2,1,5,6,1,4,7,1,2,3）                           |
| output_num      | varchar(255) | 算法输出结果（如各算法的页面置换过程、最终缺失页统计）               |
| create_time     | datetime     | 测试记录创建/更新时间，默认自动更新为当前时间                         |

#### 2. page（页面置换结果表）
关联 `test_num` 表，存储单次测试中不同页面置换算法的执行细节（按测试ID关联）。

| 字段名          | 类型 | 说明                                                                 |
|-----------------|------|----------------------------------------------------------------------|
| id              | int  | 主键，自增                                                           |
| t_id            | int  | 外键，关联 test_num.id，级联删除/更新                                |
| FIFO_time       | int  | FIFO算法总耗时（基于内存/TLB/缺页耗时计算）                          |
| FIFO_losepage   | int  | FIFO算法页面缺失次数                                                 |
| LRU_time        | int  | LRU算法总耗时                                                        |
| LRU-losepage    | int  | LRU算法页面缺失次数（字段名下划线/连字符注意：建议统一为 LRU_losepage） |
| LFU_time        | int  | LFU算法总耗时                                                        |
| LFU-losepage    | int  | LFU算法页面缺失次数（字段名下划线/连字符注意：建议统一为 LFU_losepage） |

### 表关系
- `page` 表通过 `t_id` 外键关联 `test_num` 表的 `id`，形成“一对多”关系（单次测试可记录多组算法结果，或按算法维度拆分记录）。
- 外键约束配置为 `ON DELETE CASCADE ON UPDATE CASCADE`，确保测试配置删除/更新时，关联的算法结果同步更新。

## 环境要求
- MySQL 8.0+（兼容 5.7+，需调整字符集/排序规则适配）
- 字符集：utf8mb4（支持全量中文/特殊字符）
- 存储引擎：InnoDB（支持事务、外键约束）

## 使用说明
### 1. 导入数据库脚本
```bash
# 登录MySQL后执行
USE mysql;
CREATE DATABASE IF NOT EXISTS pagingsystem DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE pagingsystem;
SOURCE /path/to/YSU-Operating-System-Project/pagingsystem.sql;
```

### 2. 核心操作示例
#### 插入测试配置
```sql
INSERT INTO test_num (page_num, use_TLB, TLB_num, input_num, output_num)
VALUES (4, 1, 2, '1,3,2,1,5,6,1,4,7,1,2,3', 'FIFO:缺失5次,LRU:缺失4次,LFU:缺失3次');
```

#### 插入算法执行结果
```sql
INSERT INTO page (t_id, FIFO_time, FIFO_losepage, LRU_time, `LRU-losepage`, LFU_time, `LFU-losepage`)
VALUES (1, 8500, 5, 7800, 4, 7200, 3);
```

#### 查询单次测试的完整数据
```sql
SELECT tn.*, p.FIFO_losepage, p.LRU_losepage, p.LFU_losepage
FROM test_num tn
LEFT JOIN page p ON tn.id = p.t_id
WHERE tn.id = 1;
```

## 字段命名规范说明
- 注意 `page` 表中 `LRU-losepage`/`LFU-losepage` 字段使用连字符（`-`），建议后续开发中统一为下划线（`_`）（如 `LRU_losepage`），避免编程语言解析字段名时的兼容问题。
- 所有数值型耗时字段建议统一单位（如微秒），便于算法性能对比。

## 扩展建议
1. 新增 `algorithm_type` 字段，支持扩展更多页面置换算法（如 Clock 算法）；
2. 增加 `TLB_hit_rate` 字段，直接存储 TLB 命中率，简化统计逻辑；
3. 新增索引优化查询：在 `test_num.create_time` 上建立索引，便于按时间筛选测试记录；
4. 对接应用层：结合 Python/Java 等语言编写页面置换算法逻辑，读取数据库配置并写入执行结果。

## 注意事项
1. 执行脚本前确保 MySQL 服务正常运行，且有创建数据库/表的权限；
2. 外键约束依赖 `test_num.id`，删除/更新 `test_num` 记录时会同步影响 `page` 表，请谨慎操作；
3. `input_num`/`output_num` 建议按固定格式存储（如逗号分隔），便于应用层解析页面序列。
