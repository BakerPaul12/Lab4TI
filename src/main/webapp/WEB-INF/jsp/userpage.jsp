<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <title>Zalogowany</title>
    ${css}
</head>
<body>
<header id="header"><b>HEADER</b></header>
<div class="container">
    <jsp:include page="left-panel.jsp" />
    <div class="right-panel">
        <!-- Prawy panel -->
        <div id="content">
            User page
        </div>
    </div>
</div>
<footer id="footer">FOOTER</footer>
<script src="<%= request.getContextPath() %>/scripts/script.js"></script>
<script src="<%= request.getContextPath() %>/scripts/userpage.js"></script>
</body>
</html>
