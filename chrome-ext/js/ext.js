/*******************************
* The Yada Chrome Extension
* Date: 7-18-2016
*
********************************/



(function() {
"use strict"

  let ext = angular.module('YadaExtension', ['ngRoute', 'ngCookies', 'angular-storage', 'angular-jwt'])

    //Router
    .config(['$routeProvider',function($routeProvider) {

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


    }])
    .run(['$rootScope','$location', 'UserExtService', function($rootScope, $location, UserExtService) {

      //stores current url in rootScope
      $rootScope.extUrl = document.referrer;

      //defines entrance animation slide()
      let mainBox = document.getElementById('mainBox');
      let slide = function() {
        TweenMax.from(mainBox, 0.7, {left: '150%', autoAlpha: 0});
      }

      //default variables to send message to chrome ext (nothing current happening)
      let chromeId = "oceicbhfpbbeomhchbhoklfhnigpolle";
      let message = {greeting: "hello from angular land"};
      chrome.runtime.sendMessage(chromeId, message);

      // sends a request to server to check session info
      // records session info to persist between refreshes
      UserExtService.checkLogStatus();

      slide();

      // callback and listener for enableBrowserAction()
      // pretty much just used for animation at this point.
      let fromExt = function(msg, sender) {
        slide();
      }
      chrome.runtime.onMessage.addListener(fromExt);
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
