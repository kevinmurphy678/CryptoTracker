<%-- 
    Document   : header
    Created on : Oct 5, 2017, 11:02:02 AM
    Author     : kevin
    Shared header for project
--%>

<head>
  <title>Title</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="css/bootstrap.min.css">
  <link rel="stylesheet" href="css/main.css">
  
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
   <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script> 
</head>

   <% // session control
         String logged = (String)session.getAttribute("logged");
   %>  

<div class ="container">
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#">Investment Tracker</a>
    </div>
    <ul class="nav navbar-nav">
      <li class="active"><a href="index.jsp">Overview</a></li>
      
          <% if (logged!=null && !logged.matches("index")) { %>
        <li class="dropdown">
        <a class="dropdown-toggle" data-toggle="dropdown" href="#">Trades
        <span class="caret"></span></a>
        <ul class="dropdown-menu">
          <li><a href="#" data-toggle="modal" data-target="#enterTrade">Add new trade</a></li>
          <li><a href="editTrades.jsp">Edit trades</a></li>
        </ul>
        </li>
        <% } %>

    </ul>

     
   
        
        <ul class="nav navbar-nav navbar-right">
        <% if (logged!=null && !logged.matches("index")) { %>
        <span class = "navbar-text"> Welcome: <%=logged%> </span>
      <li><a href="logout.jsp"><span class="glyphicon glyphicon-user"></span>Log out</a></li>
      
      <% } else { %>
      <li><a href="#" data-toggle="modal" data-target="#signUp"  ><span class="glyphicon glyphicon-user"></span> Sign Up</a></li>
      <li><a href="#" data-toggle="modal" data-target="#login" ><span class="glyphicon glyphicon-log-in"></span> Login</a></li>
      <% } %>
      

    </ul>  
    
  </div>
</nav>
</div>