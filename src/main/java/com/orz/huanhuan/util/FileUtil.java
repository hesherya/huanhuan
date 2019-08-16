package com.orz.huanhuan.util;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * @author hesher
 */
public class FileUtil {

    public static List<Object> readFromExcel(String uri,
                                             Class<? extends BaseRowModel> clazz,
                                             int sheetNo,
                                             int headLineMun) throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(uri), StandardOpenOption.READ)) {
            return EasyExcelFactory.read(new BufferedInputStream(inputStream),
                    new Sheet(sheetNo, headLineMun, clazz));
        }
    }

    public static void writeToExcel(String uri, List<? extends BaseRowModel> models, Sheet sheet) throws IOException {
        try (OutputStream out = new FileOutputStream(uri)) {
            ExcelWriter writer = EasyExcelFactory.getWriter(out, ExcelTypeEnum.XLS, true);
            writer.write(models, sheet);
            writer.finish();
        }
    }
}
