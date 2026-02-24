package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {

    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
