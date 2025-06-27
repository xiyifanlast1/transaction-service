package org.banking.infra.base;

public abstract class ServiceBase {
    protected <T> ServiceResult<T> success()
    {
        return new ServiceResult<T>().setCode(0);
    }

    protected <T> ServiceResult<T> success(T data)
    {
        return new ServiceResult<T>().setCode(0).setData(data);
    }

    protected <T> ServiceResult<T> fail(String message)
    {
        return new ServiceResult<T>().setCode(1).setMessage(message);
    }
}

