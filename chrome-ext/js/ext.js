/*******************************
* The Yada Chrome Extension
* Date: 7-18-2016
*
********************************/



(function() {
"use strict"

  let ext = angular.module('YadaExtension', ['ngRoute', 'ngCookies', 'auth0', 'angular-storage', 'angular-jwt'])

    //Router
    .config(['$routeProvider', 'authProvider', function($routeProvider, authProvider) {

        authProvider.init({
            domain: 'theyada.auth0.com',
            clientID: 'xSXRJtMJxxV34URgJ5KKKtgl1jdrGSIV'
        });

        $routeProvider
          .when('/', {
            templateUrl: '/home.html',
            controller: 'YadaExtController',
          })
          .when('/log-in', {
            templateUrl: '/log-in.html',
            controller: 'LoginExtController',
          })
          .when('/editor', {
            templateUrl: '/editor.html',
            controller: 'EditorExtController',
          })

          //Called when login is successful
          authProvider.on('loginSuccess', ['$location', 'profilePromise', 'idToken', 'store', function($location, profilePromise, idToken, store) {
            // Successfully log in
            // Access to user profile and token
            profilePromise.then(function(profile){
              // profile
              console.log(profile);
              store.set('profile', profile);
              store.set('token', idToken);
            });
            $location.url('http://localhost:8080');
          }]);

          //Called when login fails
          authProvider.on('loginFailure', function() {
            // If anything goes wrong
            console.log("log fail");
          });
    }])
    .run(['$rootScope', 'auth', 'store', 'jwtHelper', '$location', 'UserExtService', function($rootScope, auth, store, jwtHelper, $location, UserExtService) {
      $rootScope.extUrl = document.referrer;

      // Listen to a location change event
      $rootScope.$on('$locationChangeStart', function() {
        // Grab the user's token
        var token = store.get('token');
        // Check if token was actually stored
        console.log(token);
        if (token) {
          // Check if token is yet to expire
          if (!jwtHelper.isTokenExpired(token)) {
            // Check if the user is not authenticated
            if (!auth.isAuthenticated) {

              // Re-authenticate with the user's profile
              // Calls authProvider.on('authenticated')
              auth.authenticate(store.get('profile'), token);
              let u = store.get('profile');
              UserExtService.setUser({
                nickname: u.nickname,
                name: u.name,
                email: u.email
              });
              console.log("in",UserExtService.getUser());
            }
          } else {
            console.log("hai");
            // Either show the login page
            // UserService.getLogStatus();
            $location.path('/');
            // .. or
            // or use the refresh token to get a new idToken
            // auth.refreshIdToken(token);
          }
        }

      });
    }])


      // Services
      require('./services/user-ext-service')(ext);
      require('./services/yada-ext-service')(ext);


      // Controllers
      require('./controllers/nav-ext-controller')(ext);
      require('./controllers/login-ext-controller')(ext);
      require('./controllers/yada-ext-controller')(ext);
      require('./controllers/editor-ext-controller')(ext);


      // Filters

      // Directives


})()
