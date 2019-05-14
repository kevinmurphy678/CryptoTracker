/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function validateEmail() {
  var email = document.register.email.value;
  var email2 = document.register.email2.value;
  if (email !== email2) {
    document.getElementById("email2").setCustomValidity("Emails bad");
  } else {
    document.getElementById("email2").setCustomValidity("");
  }
}

function validatePwd() {
  var pwd = document.register.pwd.value;
  var pwd2 = document.register.pwd2.value;
  if (pwd !== pwd2) {
    document.getElementById("pwd2").setCustomValidity("Passwords do not match");
  } else {
    document.getElementById("pwd2").setCustomValidity("");
  }
}

