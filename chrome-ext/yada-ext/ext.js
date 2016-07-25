(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);throw new Error("Cannot find module '"+o+"'")}var f=n[o]={exports:{}};t[o][0].call(f.exports,function(e){var n=t[o][1][e];return s(n?n:e)},f,f.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/*******************************
* Editor Ext Controller
*
********************************/

module.exports = function(ext) {

  ext.controller('EditorExtController', ['$scope', '$rootScope','YadaExtService', function($scope, $rootScope, YadaExtService){

    console.log("hello url", $rootScope.extUrl);
    $scope.scrapedText = YadaExtService.scrapeIt($rootScope.extUrl);
    $scope.editorText = '';

    $scope.postIt = function () {
      YadaExtService.sendYada($rootScope.extUrl, $scope.editorText);
    };

  }]);
}

},{}],2:[function(require,module,exports){
/*******************************
* Login Extension Controller
* display user information from service
********************************/


module.exports = function(ext) {

  ext.controller('LoginExtController', ['$scope', 'UserExtService', function($scope, UserExtService){
    $scope.username = '';
    $scope.userObj = UserExtService.getUser();


      $scope.login = function() {
        //start session
        //block user input *ADD* condition if user has been created
        console.log($scope.username);
        if ($scope.username === '' || $scope.password === '') {
          console.log("enter your password right", $scope.username);
          return
        } else {
            UserExtService.setUser({username: $scope.username, password: $scope.password});
            $scope.username = '';
            $scope.password = '';
        }
      }


    $scope.logout = function() {
      //clear session
      UserExtService.clearSession();
    }




  }])
}

},{}],3:[function(require,module,exports){
/*******************************
* Nav Ext Controller
*
********************************/

module.exports = function(ext) {

  ext.controller('NavExtController', ['$scope', '$rootScope','UserExtService', function($scope, $rootScope, UserExtService){


    /*******************************
    * menu collapse
    *********************************/
    $scope.logStatus = UserExtService.getLogStatus();
    $scope.isCollapsed = false;

    $scope.toWebsite = function() {
      let win = window.open("http://localhost:8080", '_blank');
      win.focus();
    }

  }]);
}

},{}],4:[function(require,module,exports){
/*******************************
* Yada Ext Controller
* display yadas from the current Url
********************************/

module.exports = function(ext) {

  ext.controller('YadaExtController', ['$scope', '$rootScope','YadaExtService', function($scope, $rootScope, YadaExtService){

      
      $scope.yadas = YadaExtService.getYadas($rootScope.extUrl);

  }]);
}

},{}],5:[function(require,module,exports){
'use strict';

/*******************************
* The Yada Chrome Extension
* Date: 7-18-2016
*
********************************/

(function () {
  "use strict";

  var ext = angular.module('YadaExtension', ['ngRoute'])

  //Router
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/', {
      templateUrl: '/home.html',
      controller: 'YadaExtController'
    }).when('/log-in', {
      templateUrl: '/log-in.html',
      controller: 'LoginExtController'
    }).when('/log-out', {
      templateUrl: '/log-out.html',
      controller: 'LoginExtController'
    }).when('/editor', {
      templateUrl: '/editor.html',
      controller: 'EditorExtController'
    });
  }]).run(function ($rootScope) {
    $rootScope.extUrl = document.referrer;
  });

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

})();
},{"./controllers/editor-ext-controller":1,"./controllers/login-ext-controller":2,"./controllers/nav-ext-controller":3,"./controllers/yada-ext-controller":4,"./services/user-ext-service":6,"./services/yada-ext-service":7}],6:[function(require,module,exports){
/*******************************
* User Extension Service
* stores user
********************************/


module.exports = function(ext) {

  ext.factory('UserExtService', ['$http', '$location', function($http, $location) {

      let userObj = {};
      let logStatus = {status: false};

      return {
        // need server and db to post
        setUser(user) {

          $http({
            url: 'http://localhost:8080/login',
            method: 'POST',
            data: user
          }).then(function() {
            angular.copy(user, userObj);
            let log = {status: true};
            angular.copy(log, logStatus);

            $location.path('/');
          })
        },


        // return log status
        getLogStatus() {

          return logStatus;
        },

        // current user
        getUser() {

          return userObj;
        },

        // clear out user information and reset status
        clearSession() {
          $http({
            url: 'http://localhost:8080/logout',
            method: 'POST',
            data: {
              user: userObj,
            }
          }).then(function() {

            user = {};
            let log = {status: false};

            angular.copy(user, userObj);
            angular.copy(log, logStatus);

            $location.path('/');
          });

        },
      }


  }])
}

},{}],7:[function(require,module,exports){
/*******************************
* Yada Ext Service
* grabs yadas from the current Url
********************************/

module.exports = function(ext) {

  ext.factory('YadaExtService', ['$http','$location', function($http, $location){

      let yadas = [];
      let scrapes = [];

      return {

        getYadas(extUrl) {

          let currentUrl = 'http://localhost:8080/lemmieSeeTheYadas?url=' + extUrl;
          $http({
              url: currentUrl,
              method: 'GET'
            }).then(function(response){
              currentYadas = response.data;
              angular.copy(currentYadas, yadas);
            })
            console.log(yadas);
            return yadas;
        },

        scrapeIt(extUrl) {

          let scrapeUrl = 'http://localhost:8080/lemmieYada?url=' + extUrl;
          $http({
              url: scrapeUrl,
              method: 'GET'
            }).then(function(response){
              currentScrapes = response.data;
              angular.copy(currentScrapes, scrapes);
            })
            console.log(scrapes);
            return scrapes;
        },

        sendYada(extUrl, yadaText) {

          $http({
            url: "http://localhost:8080/addYada",
            method: 'POST',
            data: {
              yada: {content: `${yadaText}`},
              link: {url: `${extUrl}`}
            }
          })

        }

      }
  }]);
}

},{}]},{},[5])