package com.orz.huanhuan.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hesher
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class Invoice extends BaseRowModel {
    @ExcelProperty(value = "单号", index = 0)
    private String orderNo;
    @ExcelProperty(value = "快递公司", index = 1)
    private String company;
    @ExcelProperty(value = "运单号", index = 2)
    private String logisticsOrderNo;
}
