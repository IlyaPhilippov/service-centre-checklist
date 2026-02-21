package com.servicecenter.checklist.mapper;

import com.servicecenter.checklist.dto.request.CreateOrderRequest;
import com.servicecenter.checklist.dto.request.UpdateOrderRequest;
import com.servicecenter.checklist.dto.response.CheckItemResponse;
import com.servicecenter.checklist.dto.response.CheckResultResponse;
import com.servicecenter.checklist.dto.response.OrderResponse;
import com.servicecenter.checklist.entity.CheckItem;
import com.servicecenter.checklist.entity.CheckResult;
import com.servicecenter.checklist.entity.RepairOrder;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    RepairOrder toEntity(CreateOrderRequest request);

    @Mapping(target = "checkResults", source = "checkResults")
    @Mapping(target = "status", source = "status")
    OrderResponse toResponse(RepairOrder order);

    List<OrderResponse> toResponseList(List<RepairOrder> orders);

    @Mapping(target = "checkItem", source = "checkItem")
    CheckResultResponse toCheckResultResponse(CheckResult result);

    @Mapping(target = "checkKey", source = "checkKey")
    @Mapping(target = "requiredChecks", source = "requiredChecks")
    CheckItemResponse toCheckItemResponse(CheckItem item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrderFromRequest(UpdateOrderRequest request, @MappingTarget RepairOrder order);
}
