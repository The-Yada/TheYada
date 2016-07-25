/*******************************
* The Yada Web App
* Date: 7-18-2016
*
********************************/


(function() {
"use strict"

let app = angular.module('YadaWebApp', ['ngRoute'])



  //Router
  .config(['$routeProvider', function($routeProvider) {

    $routeProvider
      .when('/', {
        templateUrl: 'home.html',
        controller: 'HomeController',
      })

      .when('/login',{
        templateUrl: 'log-in.html',
        controller: 'LoginController',
      })

      .when('/logout',{
        templateUrl: 'log-out.html',
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
  .run(function() {

  })

  // Services
  require('./services/user-service')(app);
  require('./services/yada-service')(app);


  // Controllers
  require('./controllers/nav-controller')(app);
  require('./controllers/login-controller')(app);
  require('./controllers/home-controller')(app);



  // Filters

  // Directives



})();
