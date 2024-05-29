package pk.wieik.lab4;

import java.io.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.apache.commons.csv.*;

@WebServlet(name = "pageServlet", urlPatterns = "/")
public class HelloServlet extends HttpServlet {

    private List<Country> countries = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        super.init();
        try (InputStream inputStream = getServletContext().getResourceAsStream("/WEB-INF/countries.csv");
             Reader reader = new InputStreamReader(inputStream)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);
            for (CSVRecord record : records) {
                countries.add(new Country(record.get("Country"), record.get("Region")));
            }
        } catch (IOException e) {
            throw new ServletException("Failed to load country data", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.endsWith("getCountries")) {
            getCountries(request, response);
        } else {
            URLdelivery(request, response);
        }
    }
    private void getCountries(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/xml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String countryFilter = request.getParameter("countryFilter");
        String regionFilter = request.getParameter("regionFilter");

        if (countryFilter == null) {
            countryFilter = "";
        }
        if (regionFilter == null) {
            regionFilter = "";
        }

        // Debugging: Log filters
        System.out.println("countryFilter: '" + countryFilter + "'");
        System.out.println("regionFilter: '" + regionFilter + "'");

        PrintWriter out = response.getWriter();
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<countries>");

        boolean isEmpty = countryFilter.isEmpty() && regionFilter.isEmpty();

        if (isEmpty) {
            for (Country country : countries) {
                out.println("<country>");
                out.println("<name>" + country.getName() + "</name>");
                out.println("<region>" + country.getRegion() + "</region>");
                out.println("</country>");
            }
        } else {
            for (Country country : countries) {
                if (country.getName().toLowerCase().contains(countryFilter.toLowerCase()) &&
                        country.getRegion().toLowerCase().contains(regionFilter.toLowerCase())) {
                    out.println("<country>");
                    out.println("<name>" + country.getName() + "</name>");
                    out.println("<region>" + country.getRegion() + "</region>");
                    out.println("</country>");
                }
            }
        }

        out.println("</countries>");
    }





    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String uri = request.getRequestURI();
        if (uri.endsWith("getCountries")) {
            getCountries(request, response);
        } else {
            loginDelivery(request, response);
        }
    }

    private void loginDelivery(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("login");
        String password = request.getParameter("password");
        String logout = request.getParameter("logout");

        if (isValidAdmin(username, password)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("isAdmin", true);
            session.setMaxInactiveInterval(30 * 60);
            response.sendRedirect("Logged");
            System.out.println("Admin logged in");
        } else if (isValidUser(username, password)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setMaxInactiveInterval(30 * 60);
            response.sendRedirect("Logged");
            System.out.println("User logged in");
        } else if (logout != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("index");
        } else {
            response.sendRedirect("index?error=true");
        }
    }

    private boolean isValidAdmin(String username, String password) {
        return "admin".equals(username) && "admin".equals(password);
    }

    private boolean isValidUser(String username, String password) {
        return "user".equals(username) && "user".equals(password);
    }

    private void URLdelivery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Odczytaj ciasteczka
        Cookie[] cookies = request.getCookies();
        String lastPage = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("lastSite".equals(cookie.getName())) {
                    lastPage = cookie.getValue();
                    break;
                }
            }
        }

        // Pobierz URI żądania
        String uri = request.getRequestURI();
        String page = "/WEB-INF/jsp/index.jsp"; // Domyślna strona

        // Jeśli użytkownik odwiedza stronę główną ciasteczko istnieje, przekieruj do strony zapisanej w ciasteczku
        if (uri.equals("/Lab4-1.0-SNAPSHOT/") && lastPage != null && !lastPage.equals("/Lab4-1.0-SNAPSHOT/")) {
            uri = lastPage;
        }

        // Wybierz stronę na podstawie aktualnego uri
        if (uri.endsWith("calculator")) {
            page = "/WEB-INF/jsp/calculator.jsp";
        } else if (uri.endsWith("Logged")) {
            page = "/WEB-INF/jsp/logged.jsp";
        } else if (uri.endsWith("userpage")) {
            page = "/WEB-INF/jsp/userpage.jsp";
        } else if (uri.endsWith("countries")) {
            page = "/WEB-INF/jsp/countries.jsp";
        }

        // Ustaw aktualny uri jako wartość ciasteczka
        Cookie lastSite = new Cookie("lastSite", uri);
        lastSite.setMaxAge(60 * 60);
        response.addCookie(lastSite);

        // Przygotuj atrybuty dla JSP
        String cssLink = determineCssLink(request, response);
        String username = getLoggedUserName(request);
        String errorParam = request.getParameter("error");
        String dynamicNotLogged = getString(errorParam);
        String dynamicMenuLogged = getDynamicMenuLogged();

        request.setAttribute("css", cssLink);
        request.setAttribute("USER", username);

        HttpSession session = request.getSession();
        if (session.getAttribute("username") != null) {
            request.setAttribute("MENU", dynamicMenuLogged);
        } else {
            request.setAttribute("MENU", dynamicNotLogged);
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(page);
        dispatcher.forward(request, response);
    }

    private String determineCssLink(HttpServletRequest request, HttpServletResponse response) {
        String themeParam = request.getParameter("theme");
        HttpSession session = request.getSession();
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        if (isAdmin != null && isAdmin) {
            if ("dark-theme".equals(themeParam)) {
                session.setAttribute("theme", "dark-theme");
                setCssCookie(response, "dark-theme");
                return "<link rel=\"stylesheet\" href=\"css/darkcss.css\">";
            } else if ("white-theme".equals(themeParam)) {
                session.setAttribute("theme", "white-theme");
                setCssCookie(response, "white-theme");
                return "<link rel=\"stylesheet\" href=\"css/styles.css\">";
            }
        }

        String sessionTheme = (String) session.getAttribute("theme");
        if (sessionTheme == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("theme")) {
                        sessionTheme = cookie.getValue();
                        session.setAttribute("theme", sessionTheme);
                        break;
                    }
                }
            }
        }

        if ("dark-theme".equals(sessionTheme)) {
            return "<link rel=\"stylesheet\" href=\"css/darkcss.css\">";
        } else {
            return "<link rel=\"stylesheet\" href=\"css/styles.css\">";
        }
    }

    private void setCssCookie(HttpServletResponse response, String theme) {
        Cookie cookie = new Cookie("theme", theme);
        cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private static String getString(String errorParam) {
        String dynamicNotLogged = "<div class=\"button-menu\">\n" +
                "    <label for=\"checkboxMenu\" class=\"checkbox-image\">\n" +
                "        <img src=\"images/menuButton.png\" alt=\"Checkbox\" class=\"imageMenu\">\n" +
                "    </label>\n" +
                "    <input type=\"checkbox\" id=\"checkboxMenu\" style=\"visibility: hidden;\">\n" +
                "    <div class=\"button-container\">\n" +
                "        <a href=\"index\" class=\"button\">Home</a>\n" +
                "        <a href=\"calculator\" class=\"button\">Calculator</a>\n" +
                "        <a href=\"http://google.pl\" class=\"button\">Google</a>\n" +
                "        <a href=\"countries\" class=\"button\">Countries</a>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"loginpanel\">\n" +
                "    <form action=\"PageServlet\" method=\"post\">\n" +
                "        Login:\n" +
                "        <input type=\"text\" name=\"login\">\n" +
                "        Password:\n" +
                "        <input type=\"password\" name=\"password\">\n" +
                "        <button type=\"submit\">Login</button>\n" +
                "    </form>\n";
        if (errorParam != null && errorParam.equals("true")) {
            dynamicNotLogged += "<div style=\"color: red;\">Login FAILED</div>";
        }
        dynamicNotLogged += "</div>";
        return dynamicNotLogged;
    }

    private static String getDynamicMenuLogged() {
        return "<div class=\"button-menu\">\n" +
                "    <label for=\"checkboxMenu\" class=\"checkbox-image\">\n" +
                "        <img src=\"images/menuButton.png\" alt=\"Checkbox\" class=\"imageMenu\">\n" +
                "    </label>\n" +
                "    <input type=\"checkbox\" id=\"checkboxMenu\" style=\"visibility: hidden;\">\n" +
                "    <div class=\"button-container\">\n" +
                "        <a href=\"index\" class=\"button\">Home</a>\n" +
                "        <a href=\"calculator\" class=\"button\">Calculator</a>\n" +
                "        <a href=\"http://google.pl\" class=\"button\">Google</a>\n" +
                "        <a href=\"countries\" class=\"button\">Countries</a>\n" +
                "        <a href=\"userpage\" class=\"button\">User Page</a>\n" +
                "        <form method=\"post\" action=\"login\">\n" +
                "            <input type=\"hidden\" name=\"logout\" value=\"true\">\n" +
                "            <input type=\"submit\" value=\"Logout\">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "</div>";
    }

    private static String getLoggedUserName(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        return username == null ? "" : username;
    }

    private static class Country {
        private final String name;
        private final String region;

        public Country(String name, String region) {
            this.name = name;
            this.region = region;
        }

        public String getName() {
            return name;
        }

        public String getRegion() {
            return region;
        }
    }
}
