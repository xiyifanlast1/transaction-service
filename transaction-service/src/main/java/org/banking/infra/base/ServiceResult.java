package org.banking.infra.base;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ServiceResult<T> {
    private int code;
    private String message;
    private T data;
}