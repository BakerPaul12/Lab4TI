<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <title>Zalogowany</title>
    ${css}
</head>
<body>
<jsp:include page="header.jsp" />
<div class="container">
    <jsp:include page="left-panel.jsp" />
    <div class="right-panel">
        <div id="content">
            <header style="font-size: large">Hello ${USER}!</header>

            <form id="theme-form">
                <input type="radio" name="theme" value="white-theme">
                <label for="WhiteTheme">White Theme</label><br>
                <input type="radio" name="theme" value="dark-theme">
                <label for="DarkTheme">Dark Theme</label><br>
                <br>
                <input type="submit" value="Change theme">
            </form>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp" />
<script src="${pageContext.request.contextPath}/scripts/script.js"></script>
<script src="${pageContext.request.contextPath}/scripts/userpage.js"></script>
</body>
</html>
