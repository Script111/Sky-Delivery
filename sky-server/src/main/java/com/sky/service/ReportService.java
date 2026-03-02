package com.sky.service;

import com.sky.entity.AddressBook;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    TurnoverReportVO TurnoverReport(LocalDate begin, LocalDate end);

    UserReportVO UserReport(LocalDate begin, LocalDate end);

    OrderReportVO orderStatics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
