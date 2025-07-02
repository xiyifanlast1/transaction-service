package org.banking.service.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.banking.infra.base.EntityBase;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TransactionEntity extends EntityBase {
    private String account;
    private BigDecimal amount;
    private String product;
    private String type;
    private String remark;
}
