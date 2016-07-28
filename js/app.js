/*******************************
* The Yada Web App
* Date: 7-18-2016
*
********************************/


(function() {
"use strict"

let app = angular.module('YadaWebApp', ['ngRoute', 'auth0', 'angular-storage', 'angular-jwt'])



  /*******************************
  * ROUTER
  *********************************/
  .config(['$routeProvider', 'authProvider', function($routeProvider, authProvider) {

    authProvider.init({
        domain: 'theyada.auth0.com',
        clientID: 'xSXRJtMJxxV34URgJ5KKKtgl1jdrGSIV'
    });
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
        $location.url('/');
      }]);

      //Called when login fails
      authProvider.on('loginFailure', function() {
        // If anything goes wrong
        console.log("log fail");
      });


  }])
  /*******************************
  * run function when app is initiated
  * could be used to check for cookies or user log status
  *********************************/
  .run(['$rootScope', 'auth', 'store', 'jwtHelper', '$location', function($rootScope, auth, store, jwtHelper, $location) {
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
          }
        } else {
          // Either show the login page
          // $location.path('/');
          // .. or
          // or use the refresh token to get a new idToken
          auth.refreshIdToken(token);
        }
      }

    });
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



})();
