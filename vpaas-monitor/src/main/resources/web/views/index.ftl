<#include "base/base.ftl">

<#macro page_body>

    <h2>Vpaas 长连接管理系统</h2>

    <div class="entries">
        <url>
            <li>
                <span>长连接: 机器查询</span>
                <ul>
                    <li>集群机器列表</li>
                </ul>
            </li>

            <li>
                <span>长连接: 瞬时数据</span>
                <ul>
                    <li>用户在线状态查询（瞬时）</li>
                    <li>设备在线状态查询（瞬时）</li>
                </ul>
            </li>

            <li>
                <span>长连接: 连接历史跟踪数据</span>
                <ul>
                    <li>用户在线状态查询（瞬时）</li>
                    <li>设备在线状态查询（瞬时）</li>
                </ul>
            </li>

            <li>
                <span>长连接: 工具</span>
                <ul>
                    <li>强制设备或者用户下线</li>
                </ul>
            </li>
        </url>
    </div>

</#macro>

<@display_page />