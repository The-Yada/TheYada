(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);throw new Error("Cannot find module '"+o+"'")}var f=n[o]={exports:{}};t[o][0].call(f.exports,function(e){var n=t[o][1][e];return s(n?n:e)},f,f.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/*******************************
* Home Controller
*
********************************/

module.exports = function(app) {

  app.controller('HomeController', ['$scope', 'YadaService', function($scope, YadaService){

    /*******************************
    * grab the yadas for the ng-repeat in home.html
    *********************************/
    $scope.topYadas = YadaService.getTopYadas();


    $scope.upIt = function (yada) {
        YadaService.upKarma(yada);
    }
    $scope.downIt = function (yada) {
        YadaService.downKarma(yada);
    }


  }])
}

},{}],2:[function(require,module,exports){
/*******************************
* Login Controller
* display user information from service
********************************/


module.exports = function(app) {

  app.controller('LoginController', ['$scope', 'UserService', function($scope, UserService){
    $scope.username = '';
    $scope.userObj = UserService.getUser();


      $scope.login = function() {
        //start session
        //block user input *ADD* condition if user has been created
        console.log($scope.username);
        if ($scope.username === '' || $scope.password === '') {
          console.log("enter your shit right", $scope.username);
          return
        } else {
            UserService.setUser({username: $scope.username, password: $scope.password});
            $scope.username = '';
            $scope.password = '';

        }
      }


    $scope.logout = function() {
      //clear session
      UserService.clearSession();
    }




  }])
}

},{}],3:[function(require,module,exports){
/*******************************
* Nav Controller
*
********************************/

module.exports = function(app) {

  app.controller('NavController', ['$scope', 'UserService', function($scope, UserService){

    /*******************************
    * menu collapse
    *********************************/
    $scope.logStatus = UserService.getLogStatus();
    // $scope.user = UserService.getUser();
    // $scope.isCollapsed = false;







  }])
}

},{}],4:[function(require,module,exports){
'use strict';

/*******************************
* The Yada Web App
* Date: 7-18-2016
*
********************************/

(function () {
  "use strict";

  var app = angular.module('YadaWebApp', ['ngRoute'])

  //Router
  .config(['$routeProvider', function ($routeProvider) {

    $routeProvider.when('/', {
      templateUrl: 'home.html',
      controller: 'HomeController'
    }).when('/login', {
      templateUrl: 'log-in.html',
      controller: 'LoginController'
    }).when('/logout', {
      templateUrl: 'log-out.html',
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
  require('./services/yada-service')(app);

  // Controllers
  require('./controllers/nav-controller')(app);
  require('./controllers/login-controller')(app);
  require('./controllers/home-controller')(app);

  // Filters

  // Directives

})();
},{"./controllers/home-controller":1,"./controllers/login-controller":2,"./controllers/nav-controller":3,"./services/user-service":5,"./services/yada-service":6}],5:[function(require,module,exports){
/*******************************
* User Service
* stores user
********************************/


module.exports = function(app) {

  app.factory('UserService', ['$http', '$location', function($http, $location) {

      let userObj = {};
      let logStatus = {status: false};

      return {
        // need server and db to post
        setUser(user) {

          $http({
            url: '/login',
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
            url: '/logout',
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

},{}],6:[function(require,module,exports){
/*******************************
* Yada Service
* grabs yadaList from server
********************************/


module.exports = function(app) {


  app.factory('YadaService', ['$http', '$location', function($http, $location) {

    /*******************************
      yadaList should look list:
      [
          id: 0,
          linkScore: 0,
          numberOfYadas: 0,
          timeDiffInSeconds: 0,
          timeOfCreation:{},
          totalVotes: 0,
          url: "",
          yadaList:[{
              content: "",
              karma: 0,
              time: {},
              score: 0,
              user: "",
              link: ""
          }]
      ]
    ********************************/
      let topYadas = [];




      return {
        /*******************************
        * get yadas from server for home page
        ********************************/
        getTopYadas() {
          $http({
              url: '/theYadaList',
              method: 'GET'
            }).then(function(response){
              yadas = response.data;
              angular.copy(yadas, topYadas);
            })
            console.log(topYadas);
            return topYadas;
        },

        upKarma(yada) {
            $http({
              url: '/upVote',
              method: 'POST',
              data: `${yada}`
            })
        },
        
        downKarma(yada) {
          $http({
            url: '/downVote',
            method: 'POST',
            data: `${yada}`
          })
        }
      }


  }])
}

},{}]},{},[4])