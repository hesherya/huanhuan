package com.orz.huanhuan.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hesher
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AliOrder extends BaseRowModel {
    @ExcelProperty(index = 13)
    private String consignee;
    @ExcelProperty(index = 14)
    private String address;
    @ExcelProperty(index = 17)
    private String mobilePhone;
    @ExcelProperty(index = 18)
    private String productName;
    @ExcelProperty(index = 19)
    private String price;
    @ExcelProperty(index = 20)
    private String count;
    @ExcelProperty(index = 27)
    private String comment;
    /**
     * 物流单号
     */
    @ExcelProperty(index = 28)
    private String logisticsOrder;
}
