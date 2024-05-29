<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <title>Strona główna</title>
    ${css}
</head>
<body>
<jsp:include page="header.jsp" />
<div class="container">
    <jsp:include page="left-panel.jsp" />
    <jsp:include page="right-panel.jsp" />
</div>
<jsp:include page="footer.jsp" />
<script src="${pageContext.request.contextPath}/scripts/script.js"></script>
</body>
</html>
