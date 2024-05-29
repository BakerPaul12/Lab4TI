<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Countries</title>
    ${css}
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            $("#countryFilter, #regionFilter").on('input', function() {
                getCountries();
            });

            function getCountries() {
                var countryFilter = $("#countryFilter").val();
                var regionFilter = $("#regionFilter").val();

                // Wysłanie żądania AJAX do serwera
                $.ajax({
                    type: "POST",
                    url: "getCountries",
                    contentType: 'application/x-www-form-urlencoded',
                    data: { countryFilter: countryFilter, regionFilter: regionFilter },
                    dataType: "xml",
                    success: function(response) {
                        $("#countriesTable").empty(); // Wyczyść tabelę przed dodaniem nowych danych

                        // Dodaj nagłówki tabeli
                        $("#countriesTable").append("<tr><th>Country</th><th>Region</th></tr>");

                        // Parsowanie odpowiedzi XML i dodawanie danych do tabeli
                        $(response).find("country").each(function() {
                            var name = $(this).find("name").text();
                            var region = $(this).find("region").text();
                            $("#countriesTable").append("<tr><td>" + name + "</td><td>" + region + "</td></tr>");
                        });
                    },
                    error: function(xhr, status, error) {
                        console.log("AJAX error: " + status + " - " + error);
                    }
                });
            }

            // Wywołanie funkcji getCountries() przy ładowaniu strony
            getCountries();
        });
    </script>




</head>
<body>
<jsp:include page="header.jsp" />
<div class="container">
    <jsp:include page="left-panel.jsp" />
    <div class="right-panel">
        <h2>Countries</h2>
        <form>
            <label for="countryFilter">Country:</label>
            <input type="text" id="countryFilter" name="countryFilter">
            <label for="regionFilter">Region:</label>
            <input type="text" id="regionFilter" name="regionFilter">
        </form>
        <table id="countriesTable">
            <tr>
                <th>Country</th>
                <th>Region</th>
            </tr>
            <!-- Tabela zostanie uzupełniona przez JavaScript -->
        </table>
    </div>
</div>
<jsp:include page="footer.jsp" />
</body>
</html>
