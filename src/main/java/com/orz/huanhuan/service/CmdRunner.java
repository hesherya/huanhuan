package com.orz.huanhuan.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hesher
 */
@Component
@Slf4j
@Profile("!test")
public class CmdRunner implements CommandLineRunner {
    private static final int LEAST_ARGS_NUMBER = 2;
    private final OrderService orderService;

    public CmdRunner(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) {
        if (args.length < LEAST_ARGS_NUMBER) {
            log.warn("Invalid args");
            throw new IllegalArgumentException("Invalid args.");
        }

        String huanhuanOrderFilePath = args[0];
        String alibabaOrderFilePath = args[1];

        String invoiceOrderFilePath = "./invoice_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + ".xls";
        if (args.length > LEAST_ARGS_NUMBER) {
            invoiceOrderFilePath = args[2];
        }

        orderService.generateInvoiceFile(huanhuanOrderFilePath, alibabaOrderFilePath, invoiceOrderFilePath);
    }
}
