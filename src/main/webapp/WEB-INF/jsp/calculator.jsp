<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <title>Kalkulator</title>
    ${css}
</head>
<body>
<jsp:include page="header.jsp" />
<div class="container">
    <jsp:include page="left-panel.jsp" />
    <jsp:include page="right-calculator.jsp"/>
</div>
<jsp:include page="footer.jsp" />
<script src="${pageContext.request.contextPath}/scripts/calculator.js"></script>
<script src="${pageContext.request.contextPath}/scripts/script.js"></script>
</body>
</html>
