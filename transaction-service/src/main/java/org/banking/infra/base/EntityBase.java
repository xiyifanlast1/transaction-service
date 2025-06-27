package org.banking.infra.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class EntityBase {
    private String id;
    private Instant createdTime;
}
