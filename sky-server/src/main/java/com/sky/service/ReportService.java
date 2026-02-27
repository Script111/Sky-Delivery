package com.sky.service;

import com.sky.entity.AddressBook;
import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    TurnoverReportVO report(LocalDate begin, LocalDate end);
}
