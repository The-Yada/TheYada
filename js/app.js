/*******************************
* The Yada App
* Date: 7-18-2016
* 
********************************/


(function() {
"use strict"

let app = angular.module('YadaApp', ['ngRoute'])



  //Router
  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: '',
        // controller: '',
      })

      .when('/login',{
        templateUrl: 'login.html',
        controller: 'LoginController',
      })

      .when('/logout',{
        templateUrl: 'logout.html',
        controller: 'LoginController',
      })

      .when('/about', {
        templateUrl: 'about.html',
        // controller: '',
      })

      .when('/yadayada', {
        templateUrl: 'yadayada.html',
        // controller: '',
      })

      .otherwise({
        redirectTo: '/404',
      })
  }])

  // Services
  require('./services/user-service')(app);

  // Controllers
  require('./controllers/nav-controller')(app);
  require('./controllers/login-controller')(app);



  // Filters

  // Directives



})();
