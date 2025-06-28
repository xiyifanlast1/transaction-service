package org.banking.stress;

import lombok.extern.slf4j.Slf4j;
import org.banking.service.dtos.CreateTransactionInput;
import org.banking.service.dtos.UpdateTransactionInput;
import org.banking.service.implement.TransactionService;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TransactionServiceStressTest {
    @Autowired
    private TransactionService service;

    @Rule
    public ContiPerfRule contiPerfRule = new ContiPerfRule();

    @Test
    @PerfTest(threads = 100, invocations = 10000)
    @Required(max = 200, average = 50, throughput = 500)
    public void get_test() {
        service.get("abc");
    }

    @Test
    @PerfTest(invocations = 1000, threads = 50)
    @Required(max = 500, average = 100, throughput = 100)
    public void create_test() {
        service.create(new CreateTransactionInput()
                .setAccount("acc0001")
                .setType("BUY")
                .setProduct("Apple")
                .setAmount(BigDecimal.valueOf(100)));
    }

    @Test
    @PerfTest(invocations = 3000, threads = 100)
    @Required(max = 1000, average = 120, throughput = 150)
    public void find_test() {
        service.find(0, 100);
    }

    @Test
    @PerfTest(invocations = 2000, threads = 50)
    @Required(max = 500, average = 90, throughput = 100)
    public void update_test() {
        var data=service.create(new CreateTransactionInput()
                .setAccount("acc0001")
                .setType("BUY")
                .setProduct("Apple")
                .setAmount(BigDecimal.valueOf(100))).getData();
        service.update(new UpdateTransactionInput()
                .setId(data.getId())
                .setType("SELL")
                .setAmount(BigDecimal.valueOf(200))
                .setRemark("updated"));
    }

    @Test
    @PerfTest(invocations = 1500, threads = 100)
    @Required(max = 1000, average = 70, throughput = 100)
    public void delete_test() {
        service.delete("acc0001");
    }

    @Test
    @PerfTest(duration = 10*1000, threads = 100) // run for 10s
    @Required(average = 100, throughput = 500)
    public void mixed_test() {
        Random random = new Random();
        int operation = random.nextInt(5);

        switch (operation) {
            case 0:
                create_test();
                break;
            case 1:
                get_test();
                break;
            case 2:
                find_test();
                break;
            case 3:
                update_test();
                break;
            case 4:
                delete_test();
                break;
        }
    }
}
