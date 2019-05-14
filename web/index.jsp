<%-- 
    Document   : index
    Created on : Oct 4, 2017, 9:20:31 PM
    Author     : kevin
--%>
<%@page import="java.util.Map"%>
<%@page import="com.kevin.AssetManager"%>
<%@page import="com.kevin.DBConnect"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="header.jsp"%>
<body>
<script src ="formValidate.js"></script>
<html lang="en">  

    
         <%@include file="popups.jsp"%>
    
<div class="container">
     <% if (logged!=null && !logged.matches("index")) { %>
     <%=dbConnect.portfolio(logged) %>
    <% } else { %>
        <h1>Welcome!<small> Please login to continue...</small></h1>
    <% } %>
</div>
</body>
</html>
