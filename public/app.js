(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);throw new Error("Cannot find module '"+o+"'")}var f=n[o]={exports:{}};t[o][0].call(f.exports,function(e){var n=t[o][1][e];return s(n?n:e)},f,f.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/*******************************
* Login Controller
* display user information from service
********************************/


module.exports = function(app) {

  app.controller('LoginController', ['$scope', 'UserService', '$auth', function($scope, UserService, $auth){
    $scope.username = '';
    $scope.user = UserService.getUser();

    $scope.login = function() {
      // OAuth stuff
      UserService.getUser()

    }

    $scope.authenticate = function(provider) {
      $auth.authenticate(provider);
      console.log(provider);
    };



    $scope.logout = function() {
      //clear session
      UserService.clearSession()

    }




  }])
}

},{}],2:[function(require,module,exports){
/*******************************
* Nav Controller
*
********************************/

module.exports = function(app) {

  app.controller('NavController', ['$scope', 'UserService', function($scope, UserService){

    /*******************************
    * menu collapse
    *********************************/
    // $scope.logStatus = UserService.getLogStatus();
    // $scope.user = UserService.getUser();
    $scope.isCollapsed = false;







  }])
}

},{}],3:[function(require,module,exports){
'use strict';

/*******************************
* The Yada Web App
* Date: 7-18-2016
*
********************************/

(function () {
  "use strict";

  var app = angular.module('YadaWebApp', ['ngRoute', 'satellizer'])

  //Router
  .config(['$routeProvider', '$authProvider', function ($routeProvider, $authProvider) {

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

    $routeProvider.when('/', {
      templateUrl: 'home.html'
    }).when('/login', {
      templateUrl: 'login.html',
      controller: 'LoginController'
    }).when('/logout', {
      templateUrl: 'logout.html',
      controller: 'LoginController'
    }).when('/about', {
      templateUrl: 'about.html'
    }).when('/yadayada', {
      templateUrl: 'yadayada.html'
    }).otherwise({
      redirectTo: '/404'
    });
  }]);

  // Services
  require('./services/user-service')(app);

  // Controllers
  require('./controllers/nav-controller')(app);
  require('./controllers/login-controller')(app);

  // Filters

  // Directives

})();
},{"./controllers/login-controller":1,"./controllers/nav-controller":2,"./services/user-service":4}],4:[function(require,module,exports){
/*******************************
* User Service
* stores user
********************************/


module.exports = function(app) {

  app.factory('UserService', ['$http', '$location', function($http, $location) {

      let user = {};
      let logStatus = {status: false};

      return {
        getUser() {

        },
        clearSession() {

        },
      }


  }])
}

},{}]},{},[3])