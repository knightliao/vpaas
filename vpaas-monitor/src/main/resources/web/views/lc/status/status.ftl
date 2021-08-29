<#include "../../base/base.ftl">

<#macro page_body>

    <h2>LC - status</h2>

    <h3>Client-status:</h3>
    <div class="entries">
        <div class="cell item">
            ${(sessionStoreDtoClient)!""}
        </div>
    </div>

    <h3>Client-token:</h3>
    <div class="entries">
        <div class="cell item">
            ${(token)!""}
        </div>
    </div>

    <h3>User-status:</h3>
    <div class="entries">
        <div class="cell item">
            <#list sessionStoreDtoListUid as item>
                <tr width="auto" valign="middle">
                    <div>${item}</div>
                </tr>
            </#list>
        </div>
    </div>

</#macro>

<@display_page />