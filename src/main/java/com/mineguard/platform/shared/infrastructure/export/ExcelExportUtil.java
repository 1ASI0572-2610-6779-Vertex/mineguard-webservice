package com.mineguard.platform.shared.infrastructure.export;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Minimal shared helper for rendering a flat table as an .xlsx workbook, used by the
 * {@code ?format=xls} exports (audit log, driver reports). Deliberately does not call
 * {@code Sheet#autoSizeColumn} — it depends on AWT font metrics that may be unavailable in a
 * headless container, and column width is cosmetic, not worth a deploy-time crash risk.
 */
public final class ExcelExportUtil {

    public static final String XLSX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private ExcelExportUtil() {
    }

    public static byte[] toXlsx(String sheetName, List<String> headers, List<List<String>> rows) {
        try (var workbook = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet(sheetName);
            var headerRow = sheet.createRow(0);
            for (int col = 0; col < headers.size(); col++) {
                headerRow.createCell(col).setCellValue(headers.get(col));
            }
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                var row = sheet.createRow(rowIndex + 1);
                var values = rows.get(rowIndex);
                for (int col = 0; col < values.size(); col++) {
                    row.createCell(col).setCellValue(values.get(col));
                }
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to generate Excel export", e);
        }
    }
}
