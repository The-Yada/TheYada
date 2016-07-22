/*******************************
* The Yada Web App
* Date: 7-18-2016
*
********************************/


(function() {
"use strict"

let app = angular.module('YadaWebApp', ['ngRoute','satellizer'])



  //Router
  .config(['$routeProvider', '$authProvider', function($routeProvider, $authProvider) {


    $authProvider.google({
      url: '/auth/google',
      clientId: '501527334807-ha9jues5c0u7o3ufev8a66s2jvq8gj0g.apps.googleusercontent.com',
      authorizationEndpoint: 'https://accounts.google.com/o/oauth2/auth',
      redirectUri: window.location.origin,
      requiredUrlParams: ['scope'],
      optionalUrlParams: ['display'],
      scope: ['profile', 'email'],
      scopePrefix: 'openid',
      scopeDelimiter: ' ',
      display: 'popup',
      type: '2.0',
      popupOptions: { width: 452, height: 633 }
    });

    $routeProvider
      .when('/', {
        templateUrl: 'home.html',
        // controller: 'LoginController',
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
