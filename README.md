# vpaas
an another long-connection(lc) platform

又一个支持千万级连接的长连接平台。

[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Build Status](https://travis-ci.com/knightliao/vpaas.svg?branch=main)](https://travis-ci.com/knightliao/Vpaas) 
[![Coverage Status](https://coveralls.io/repos/github/knightliao/vpaas/badge.svg)](https://coveralls.io/github/knightliao/vpaas)

### 代码目录

- vpaas-common        共用的一些基础类
- vpaas-lc            长连接服务
    - vpaas-lc-client 长连接客户端代码
    - vpaas-lc-server 长连接服务端代码
- vpaas-demos         demos & benchmark
  - vpaas-lc-client-benchmark   长连接客户端模拟 demo & benchamark 
- vpaas-osb(online service bus) 长连接在线服务总线（包括消息推送、消息回执）
- vpaas-tui                     推送服务（包括在线(有序/无序)、离线、超高并发度等）
- vpaas-monitor                 vpaas监控&管理
