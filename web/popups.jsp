<%-- 
    Document   : popups
    Created on : Dec 20, 2017, 12:36:49 PM
    Author     : kevin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!--REGISTER POPUP-->

<div id="signUp" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Sign Up</h4>
      </div>
      <div class="modal-body">
        <p>Please fill out the information below.</p>

        <form name = "register" action = "register.jsp">
         <div class="form-group">
           <label for="email">Email address:</label>
           <input type="email" class="form-control" id="email" name = "email" required>
         </div>
         <div class="form-group">
           <label for="pwd">Password:</label>
           <input type="password" class="form-control" id="pwd" name="pwd" required>
         </div>
         <div class="form-group">
           <label for="pwd">Verify Password:</label>
           <input type="password" class="form-control" id="pwd2" required onInput="validatePwd()">
         </div>
         <button type="submit" class="btn btn-primary">Submit</button>
       </form>
      </div>    
    </div>
  </div>
</div>    
    
<!-- LOGIN POPUP-->
<div id="login" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Log in</h4>
      </div>
      <div class="modal-body">
        <p>Please fill out the information below.</p>
        <form name = "register" action = "login.jsp">
         <div class="form-group">
           <label for="email">Email address:</label>
           <input type="email" class="form-control" id="email" name = "email" required>
         </div>
         <div class="form-group">
           <label for="pwd">Password:</label>
           <input type="password" class="form-control" id="pwd" name="pwd" required>
         </div>
         <button type="submit" class="btn btn-primary">Submit</button>
       </form>
      </div>    
    </div>
  </div>
</div>    
    
    
<!-- Trades Form -->
<div id="enterTrade" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">New Trade</h4>
      </div>
      <div class="modal-body">
        <p>Please fill out the information below.</p>

        <form name = "enterTrade" action = "enterTrade.jsp" method = "post">
         <div class="form-group">
           <label for="email">Asset</label> 
          <select class="form-control" name="asset">
              <%
                  DBConnect dbConnect = new DBConnect();
                  String sql4 = "select assetID, symbol from assets";
                %>
                <%= dbConnect.dropdown(sql4)%>
            </select>
         </div>
            
         <div class="form-group">
           <label for="pwd">Buy / Sell</label>
           <select class="form-control" name="buySell" required>
               <option>Buy</option>
               <option>Sell</option>
           </select>
         </div>   
            
         <div class="form-group">
           <label for="pwd">Date (dd-mm-yyyy)</label>
           <input type="text" class="form-control" name="date" required>
         </div>
         <div class="form-group">
           <label for="pwd">Amount</label>
           <input type="text" class="form-control" name="amount" required>
         </div>
          <div class="form-group">
           <!--<label for="pwd">Price (BTC)</label>
           <input type="text" class="form-control" name="price" required> Price not implemented-->
         </div> 
         <button type="submit" class="btn btn-primary">Add Trade</button>
       </form>
      </div>    
    </div>
  </div>
</div>   
   