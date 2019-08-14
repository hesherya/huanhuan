package com.orz.huanhuan.service;

/**
 * @author hesher
 */
public interface OrderService {
    /**
     * Generate invoice file for importing to Huanhuan.
     *
     * @param huanhuanOrderFileUri huanhuan order path.
     * @param aliOrderFileUri      alibaba order path.
     * @param invoiceFileUri       invoice order path.
     */
    void generateInvoiceFile(String huanhuanOrderFileUri,
                             String aliOrderFileUri,
                             String invoiceFileUri);
}
