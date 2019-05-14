

<%@page import="com.kevin.DBConnect"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
 
        <%@include file="header.jsp"%>
    <body>

         <%@include file="popups.jsp"%>
    
        <div class="container">
     <% if (logged!=null && !logged.matches("index")) { %>

     <%=dbConnect.editTrades(logged) %>
    <% } else { %>
        <h1>Welcome!<small> Please login to continue...</small></h1>
    <% } %>
</div>


    </body>

    
</html>
