package be.ehb.auctionhousebackend.controller;


import be.ehb.auctionhousebackend.dto.CategoryPerformanceDto;
import be.ehb.auctionhousebackend.dto.RevenueReportDto;
import be.ehb.auctionhousebackend.dto.TopBidderDto;
import be.ehb.auctionhousebackend.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Reports", description = "Auction performance and revenue reports")
@Validated
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }



    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportDto> getRevenueReport(
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateRevenueReport(startDate, endDate));
    }

    @GetMapping("/top-bidders")
    public ResponseEntity<List<TopBidderDto>> getTopBidders(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getTopBidders(limit));
    }

    @GetMapping("/category-performance")
    public ResponseEntity<List<CategoryPerformanceDto>> getCategoryPerformance() {
        return ResponseEntity.ok(reportService.getCategoryPerformance());
    }

    @GetMapping("/revenue/excel")
    public ResponseEntity<InputStreamResource> exportRevenueToExcel(
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) throws IOException {
        ByteArrayInputStream stream = reportService.exportRevenueToExcel(startDate, endDate);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Revenue_Report.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(stream));
    }
}

