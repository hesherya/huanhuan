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
public class HuanhuanOrder extends BaseRowModel {
    @ExcelProperty(index = 2)
    private String orderNo;
    @ExcelProperty(index = 6)
    private String cash;
    @ExcelProperty(index = 7)
    private String token;
    @ExcelProperty(index = 8)
    private String count;
    @ExcelProperty(index = 10)
    private String consignee;
    @ExcelProperty(index = 17)
    private String mobilePhone;
    @ExcelProperty(index = 12)
    private String province;
    @ExcelProperty(index = 13)
    private String city;
    @ExcelProperty(index = 14)
    private String county;
    @ExcelProperty(index = 15)
    private String address;
    @ExcelProperty(index = 1)
    private String productName;
    @ExcelProperty(index = 23)
    private String status;
    @ExcelProperty(index = 19)
    private String logisticOrderNo;
}
