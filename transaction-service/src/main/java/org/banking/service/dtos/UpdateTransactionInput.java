package org.banking.service.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class UpdateTransactionInput {
    private String id;
    private String type;
    private BigDecimal amount;
    private String remark;
}
