package com.orz.huanhuan.service;

import com.alibaba.excel.metadata.Sheet;
import com.orz.huanhuan.model.AliOrder;
import com.orz.huanhuan.model.HuanhuanOrder;
import com.orz.huanhuan.model.HuanhuanOrderStatus;
import com.orz.huanhuan.model.Invoice;
import com.orz.huanhuan.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hesher
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final String STRING_SINGLE_SPACE = " ";
    private static final int ADDRESS_INDEX = 3;
    private final OrderRepository huanhuanOrderRepository;
    private final OrderRepository aliOrderRepository;
    private final OrderRepository invoiceRepository;


    public OrderServiceImpl(@Qualifier("huanhuanOrderRepository") OrderRepository huanhuanOrderRepository,
                            @Qualifier("aliOrderRepository") OrderRepository aliOrderRepository,
                            @Qualifier("invoiceRepository") OrderRepository invoiceRepository) {
        this.huanhuanOrderRepository = huanhuanOrderRepository;
        this.aliOrderRepository = aliOrderRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public void generateInvoiceFile(String huanhuanOrderFileUri,
                                    String aliOrderFileUri,
                                    String invoiceFileUri) {
        List<Object> huanhuanOrders;
        try {
            huanhuanOrders = huanhuanOrderRepository.readOrdersFromExcel(huanhuanOrderFileUri,
                    HuanhuanOrder.class, 1, 1);
        } catch (IOException e) {
            log.error("IO exception occurred", e);
            throw new RuntimeException(e);
        }

        List<Object> aliOrders;
        try {
            aliOrders = aliOrderRepository.readOrdersFromExcel(aliOrderFileUri,
                    AliOrder.class, 1, 1);
        } catch (IOException e) {
            log.error("IO exception occurred", e);
            throw new RuntimeException(e);
        }

        // Convert to map and matching.
        List<Invoice> invoices = getInvoices(huanhuanOrders, convertAliOrdersToMap(aliOrders), aliOrders);

        if (invoices.isEmpty()) {
            log.info("No order to be matched.");
            return;
        } else {
            log.info("output ----->\n{}", invoices);
        }

        writeToFile(invoiceFileUri, invoices);
    }

    private Map<String, AliOrder> convertAliOrdersToMap(List<Object> aliOrders) {
        return Objects.requireNonNull(aliOrders)
                .stream()
                .filter(o -> StringUtils.isNotBlank(((AliOrder) o).getComment()))
                .collect(Collectors.toMap(order -> ((AliOrder) order).getComment(),
                        o -> (AliOrder) o,
                        (k1, k2) -> {
                            k1.setCount(Integer.toString(Integer.valueOf(k1.getCount()) + Integer.valueOf(k2.getCount())));
                            k1.setComment(k1.getComment() + "," + k2.getComment());
                            return k1;
                        }));
    }

    private List<Invoice> getInvoices(List<Object> huanhuanOrders,
                                      Map<String, AliOrder> aliOrderMap,
                                      List<Object> aliOrders) {
        List<Invoice> invoices = new ArrayList<>(huanhuanOrders.size());
        checking(huanhuanOrders, aliOrderMap, invoices, aliOrders);
        return invoices;
    }

    @SuppressWarnings("AlibabaAvoidComplexCondition")
    private void checking(List<Object> huanhuanOrders,
                          Map<String, AliOrder> aliOrderMap,
                          List<Invoice> invoices,
                          List<Object> aliOrders) {
        Objects.requireNonNull(huanhuanOrders)
                .stream()
                .filter(o -> {
                    HuanhuanOrder order = (HuanhuanOrder) o;
                    return StringUtils.isBlank(order.getLogisticOrderNo()) &&
                            (order.getStatus().contains(HuanhuanOrderStatus.WAIT_TO_SHIP.getDesc())
                                    || order.getStatus().contains(HuanhuanOrderStatus.SHIPPING.getDesc()));
                })
                .forEach(o -> {
                    HuanhuanOrder order = (HuanhuanOrder) o;
                    if (aliOrderMap.containsKey(order.getOrderNo())) {
                        AliOrder aliOrder = aliOrderMap.get(order.getOrderNo());
                        doChecking(invoices, order, aliOrder);
                    } else {
                        log.warn("[{}] not exits in ali order, try to match.", order.getOrderNo());
                        // 多单合并发货的情况，保留第一行数据即可，运单号一致。
                        // Match consignee, mobile phone number, address.
                        // Build invoice and add to list.
                        aliOrders.stream()
                                // 多单合并发货的情况，保留第一行数据即可，运单号一致。
                                .filter(od -> StringUtils.isNotBlank(((AliOrder) od).getConsignee()))
                                .map(od -> (AliOrder) od)
                                .filter(aliOrder -> aliOrder.getConsignee().equalsIgnoreCase(order.getConsignee())
                                        && aliOrder.getMobilePhone().equalsIgnoreCase(order.getMobilePhone())
                                        && (((order.getProvince() + STRING_SINGLE_SPACE
                                        + order.getCity() + STRING_SINGLE_SPACE
                                        + order.getCounty() + STRING_SINGLE_SPACE
                                        + order.getAddress()).equalsIgnoreCase(aliOrder.getAddress()))
                                        || order.getAddress().contains(aliOrder.getAddress().split(STRING_SINGLE_SPACE)[ADDRESS_INDEX])
                                        || aliOrder.getAddress().contains(order.getAddress())))
                                .map(aliOrder -> Invoice.builder()
                                        .orderNo(order.getOrderNo())
                                        .company(aliOrder.getLogisticsOrder().split(":")[0])
                                        .logisticsOrderNo(aliOrder.getLogisticsOrder().split(":")[1])
                                        .build())
                                .forEach(invoices::add);
                    }
                });
    }

    private void doChecking(List<Invoice> invoices, HuanhuanOrder order, AliOrder aliOrder) {
        // Checking count.
        if (!order.getCount().equalsIgnoreCase(aliOrder.getCount())) {
            log.warn("[{}]'s count [{}] is different from alibaba order's [{}].",
                    order.getOrderNo(), order.getCount(), aliOrder.getCount());
            throw new IllegalArgumentException("Count is different.");
        }

        // Checking consignee.
        if (!order.getConsignee().equalsIgnoreCase(aliOrder.getConsignee())) {
            log.warn("[{}]'s consignee [{}] is different from alibaba order's [{}].",
                    order.getOrderNo(), order.getConsignee(), aliOrder.getConsignee());
            throw new IllegalArgumentException("Consignee is different.");
        }

        // Checking mobile phone number.
        if (!order.getMobilePhone().equalsIgnoreCase(aliOrder.getMobilePhone())) {
            log.warn("[{}]'s consignee [{}] is different from alibaba order's [{}].",
                    order.getOrderNo(), order.getMobilePhone(), aliOrder.getMobilePhone());
            throw new IllegalArgumentException("Mobile phone number is different.");
        }

        // Checking address.
        if ((!(order.getProvince() + STRING_SINGLE_SPACE
                + order.getCity() + STRING_SINGLE_SPACE
                + order.getCounty() + STRING_SINGLE_SPACE
                + order.getAddress())
                .equalsIgnoreCase(aliOrder.getAddress()))
                && !order.getAddress().contains(aliOrder.getAddress().split(STRING_SINGLE_SPACE)[ADDRESS_INDEX])
                && !aliOrder.getAddress().contains(order.getAddress())) {
            log.warn("[{}]'s address [{}] is different from alibaba order's [{}].",
                    order.getOrderNo(), order.getAddress(), aliOrder.getAddress());
            throw new IllegalArgumentException("Address is different.");
        }

        if (StringUtils.isNotBlank(aliOrder.getLogisticsOrder())) {
            invoices.add(Invoice.builder()
                    .orderNo(order.getOrderNo())
                    .company(aliOrder.getLogisticsOrder().split(":")[0])
                    .logisticsOrderNo(aliOrder.getLogisticsOrder().split(":")[1])
                    .build());
        }
    }

    private void writeToFile(String invoiceFileUri, List<Invoice> invoices) {
        Sheet sheet = new Sheet(1, 1, Invoice.class);
        try {
            invoiceRepository.writeToExcel(invoiceFileUri, invoices, sheet);
        } catch (IOException e) {
            log.error("IO exception occurred", e);
            throw new RuntimeException(e);
        }
    }
}
