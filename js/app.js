/*******************************
* The Yada Web App
* Date: 7-18-2016
*
********************************/


(function() {
"use strict"

let app = angular.module('YadaWebApp', ['ngRoute', 'ngAnimate'])



  /*******************************
  * ROUTER
  *********************************/
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
  /*******************************
  * run function when app is initiated
  * could be used to check for cookies or user log status
  *********************************/
  .run(['$rootScope', '$location', 'UserService', function($rootScope, $location, UserService) {

      UserService.checkLogStatus();

  }])

  /*******************************
  * file tree of requirements
  *********************************/
  // Services
  require('./services/user-service')(app);
  require('./services/yada-service')(app);


  // Controllers
  require('./controllers/nav-controller')(app);
  require('./controllers/login-controller')(app);
  require('./controllers/home-controller')(app);



  // Filters
  require('./filters/search-filter.js')(app);

  // Directives
  // Animation
  require('./animations.js')(app);



})();
