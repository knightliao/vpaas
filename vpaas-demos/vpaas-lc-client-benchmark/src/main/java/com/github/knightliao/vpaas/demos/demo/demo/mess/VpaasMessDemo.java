package com.github.knightliao.vpaas.demos.demo.demo.mess;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/10/28 21:33
 */
@Service
@Slf4j
public class VpaasMessDemo implements IMessageInterface, ApplicationRunner {

    @Override
    public boolean checkContinue() {
        return false;
    }

    @Override
    public void newClients(int total, int shard) {

    }

    @Override
    public void newAllClients() {

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
