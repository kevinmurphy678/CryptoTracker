<%-- 
    Document   : logout
    Created on : Nov 14, 2017, 1:54:01 PM
    Author     : kevin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <%
           session.setAttribute("logged", "index");
           response.sendRedirect("index.jsp");
        %>
</html>
