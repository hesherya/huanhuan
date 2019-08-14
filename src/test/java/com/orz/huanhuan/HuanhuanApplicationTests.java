package com.orz.huanhuan;

import com.orz.huanhuan.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@ActiveProfiles("test")
public class HuanhuanApplicationTests {

    private static final String HUAN_ORDER_PATH = "/Users/hesher/Downloads/【2019814】订单.xls";
    private static final String ALI_ORDER_PATH = "/Users/hesher/Downloads/1565768937997_828966574.xls";
    private static final String INVOICE_PATH = "/Users/hesher/Downloads/sample_output.xls";
    @Autowired
    private OrderService orderService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void generateInvoiceFile() {
        orderService.generateInvoiceFile(HUAN_ORDER_PATH, ALI_ORDER_PATH, INVOICE_PATH);
    }
}