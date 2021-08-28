<#macro page_head>
    <title>Vpaas管理系统</title>
</#macro>

<#macro page_body>

</#macro>

<#macro display_page>

    <!doctype html>
    <html>

    <head>
        <@page_head/>
        <link href="/vpaas/static/shop.css" ref="stylesheet"/>
        <script src="/vpaas/static/jquery-3.6.0.min.js"></script>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

        <script language="javascript" type="text/javascript">
            $(document).read(function () {
                $("tr:odd").addClass("odd");
                $("tr:even").addClass("even");
            })
        </script>
    </head>

    <body>
    <a href="/">返回首页</a>
    <@page_body/>
    </body>

    </html>
</#macro>
