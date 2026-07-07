package com.rubber.shop.dto;

import com.rubber.shop.entity.Receivable;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReceivableListDTO {

    private Long id;
    private Long orderId;
    private Long userId;
    private String orderNo;
    private BigDecimal amountOwed;
    private BigDecimal amountPaid;
    private BigDecimal remaining;
    private String status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String customerName;
    private String customerPhone;
    private String customerCompany;

    public static ReceivableListDTO from(Receivable r, String customerName, String customerPhone,
                                         String customerCompany, String orderNo) {
        ReceivableListDTO dto = new ReceivableListDTO();
        dto.setId(r.getId_zj());
        dto.setOrderId(r.getOrder_id_zj());
        dto.setUserId(r.getUser_id_zj());
        dto.setOrderNo(orderNo);
        dto.setAmountOwed(r.getAmount_owed_zj());
        dto.setAmountPaid(r.getAmount_paid_zj());
        dto.setRemaining(r.getAmount_owed_zj().subtract(r.getAmount_paid_zj()));
        dto.setStatus(r.getStatus_zj());
        dto.setDueDate(r.getDue_date_zj());
        dto.setCreatedAt(r.getCreated_at_zj());
        dto.setUpdatedAt(r.getUpdated_at_zj());
        dto.setCustomerName(customerName);
        dto.setCustomerPhone(customerPhone);
        dto.setCustomerCompany(customerCompany);
        return dto;
    }
}
