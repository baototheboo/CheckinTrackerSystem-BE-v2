package com.example.ctsbe.controller;

import com.example.ctsbe.dto.monthlyReport.MonthlyReportDTO;
import com.example.ctsbe.dto.staffProject.StaffInProjectDTO;
import com.example.ctsbe.entity.MonthlyReport;
import com.example.ctsbe.entity.Project;
import com.example.ctsbe.entity.StaffProject;
import com.example.ctsbe.mapper.MonthlyReportMapper;
import com.example.ctsbe.mapper.StaffProjectMapper;
import com.example.ctsbe.service.MonthlyReportService;
import com.example.ctsbe.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class MonthlyReportController {
    @Autowired
    private MonthlyReportService monthlyReportService;

    @GetMapping("/getMonthlyReport")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(@RequestParam(defaultValue = "1") int page
            , @RequestParam(defaultValue = "3") int size
            //, @RequestParam(defaultValue = "0") int staffId
            , @RequestParam(required = false) String monthYear) {
        try{
            List<MonthlyReport> list = new ArrayList<>();
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<MonthlyReport> reportsPage;
            if(monthYear == null) {
                monthYear = new DateUtil().convertLocalDateToMonthAndYear(LocalDate.now());
            }
            reportsPage = monthlyReportService.getListByMonthYear(monthYear, pageable);
            list = reportsPage.getContent();
            List<MonthlyReportDTO> listDto = list.stream().
                    map(MonthlyReportMapper::convertEntityToDto).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("list", listDto);
            response.put("currentPage", reportsPage.getNumber());
            response.put("allProducts", reportsPage.getTotalElements());
            response.put("allPages", reportsPage.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
