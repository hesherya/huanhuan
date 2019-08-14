package com.orz.huanhuan.repository;

import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.orz.huanhuan.util.FileUtil;

import java.io.IOException;
import java.util.List;

/**
 * @author hesher
 */
public interface OrderRepository {
    /**
     * Read from file.
     *
     * @param uri         invoice file path.
     * @param clazz       data object's class.
     * @param sheetNo     sheet in excel.
     * @param headLineMun head line number.
     * @return order list.
     * @throws IOException if an I/O error occurs.
     */
    default List<Object> readOrdersFromExcel(String uri,
                                             Class<? extends BaseRowModel> clazz,
                                             int sheetNo,
                                             int headLineMun) throws IOException {
        return FileUtil.readFromExcel(uri, clazz, sheetNo, headLineMun);
    }

    /**
     * Write to file.
     *
     * @param uri    file path.
     * @param models data list.
     * @param sheet  sheet in excel.
     * @throws IOException if an I/O error occurs.
     */
    default void writeToExcel(String uri,
                              List<? extends BaseRowModel> models,
                              Sheet sheet) throws IOException {
        FileUtil.writeToExcel(uri, models, sheet);
    }
}
