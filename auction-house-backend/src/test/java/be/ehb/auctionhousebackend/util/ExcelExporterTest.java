package be.ehb.auctionhousebackend.util;


import be.ehb.auctionhousebackend.dto.RevenueReportDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ExcelExporterTest {

    @Test
    public void testExportRevenue() throws IOException {
        RevenueReportDto report = new RevenueReportDto(
                12345.67,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                25L
        );

        byte[] excelBytes = ExcelExporter.exportRevenue(report);
        assertNotNull(excelBytes);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Revenue Report", sheet.getSheetName());

            // Title
            Row row0 = sheet.getRow(0);
            assertEquals("Revenue Report", row0.getCell(0).getStringCellValue());

            // Start Date
            Row row1 = sheet.getRow(1);
            assertEquals("Start Date", row1.getCell(0).getStringCellValue());
            assertEquals("2024-01-01", row1.getCell(1).getStringCellValue());

            // End Date
            Row row2 = sheet.getRow(2);
            assertEquals("End Date", row2.getCell(0).getStringCellValue());
            assertEquals("2024-12-31", row2.getCell(1).getStringCellValue());

            // Total Revenue
            Row row3 = sheet.getRow(3);
            assertEquals("Total Revenue", row3.getCell(0).getStringCellValue());
            assertEquals(12345.67, row3.getCell(1).getNumericCellValue(), 0.001);

            // Number of Auctions
            Row row4 = sheet.getRow(4);
            assertEquals("Number of Auctions", row4.getCell(0).getStringCellValue());
            assertEquals(25, (long) row4.getCell(1).getNumericCellValue());
        }
    }
}
