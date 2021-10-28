package com.github.knightliao.vpaas.demos.demo.demo.mess;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/10/28 21:32
 */
public interface IMessageInterface {

    // 确定是否继续，用于停止
    boolean checkContinue();

    void newClients(int total, int shard);

    //
    void newAllClients();
}
