(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);throw new Error("Cannot find module '"+o+"'")}var f=n[o]={exports:{}};t[o][0].call(f.exports,function(e){var n=t[o][1][e];return s(n?n:e)},f,f.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
/*******************************
* Editor Ext Controller
*
********************************/

module.exports = function(ext) {

  ext.controller('EditorExtController', ['$scope', '$rootScope', '$location', 'YadaExtService', function($scope, $rootScope, $location, YadaExtService){


    $scope.scrapedText = YadaExtService.scrapeIt($rootScope.extUrl);
    $scope.editorText = '';

    /*******************************
    * post a yada
    ********************************/
    $scope.postIt = function () {
      YadaExtService.sendYada($rootScope.extUrl, $scope.editorText, function(response) {
        if (response === "success") {
          $scope.editorText = '';
        } else {
          $scope.editorText = 'sorry you have already written a yada for this article';
        }

        $location.path('/');
      });
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

    $scope.userObj = UserExtService.getUser();


    /*******************************
    * login
    ********************************/
    $scope.login = function () {
      UserExtService.setUser({
        username: $scope.username,
        password: $scope.password
      })
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
    $scope.user = UserExtService.getUser();
    $scope.logStatus = UserExtService.getLogStatus();
    $scope.isCollapsed = false;


    /*******************************
    * Redirect to Main Website in new tab
    ********************************/
    $scope.toWebsite = function() {
      let win = window.open("http://localhost:8080", '_blank');
      win.focus();
    }

    /*******************************
    * log out and clear session
    ********************************/
    $scope.logout = function() {

      UserExtService.clearSession();
    }

  }]);
}

},{}],4:[function(require,module,exports){
/*******************************
* Yada Ext Controller
* display yadas from the current Url
********************************/

module.exports = function(ext) {

  ext.controller('YadaExtController', ['$scope', '$filter', '$rootScope','$location','YadaExtService', 'UserExtService', function($scope, $filter, $rootScope, $location, YadaExtService, UserExtService){
       UserExtService.checkLogStatus();

       $scope.yadaScrollIndex = YadaExtService.getIndex();
       $scope.yadas = YadaExtService.getYadas($rootScope.extUrl);
       $scope.userObj = UserExtService.checkLogStatus();
       $scope.yadaId = 0;

       $scope.yadaUserJoinList = UserExtService.getYadaUserJoinList();

       $scope.userVotingState = UserExtService.getUserVotingState(YadaExtService.getYadaId);
      //  $scope.voted = false;



      //  $scope.karmaStatus = function(userJoins, yadaArr, index) {
       //
      //        if(userJoins !== null) {
      //          let arr = [];
       //
      //          yadaArr.forEach(function(yada) {
      //             console.log(yada);
      //              userJoins.forEach(function(yuj){
      //                  if (yuj.theYadaId === yada.id) {
      //                    console.log("what", yuj, yada.id);
      //                    arr.push(yuj);
      //                  }
      //              })
      //         })
       //
      //         return arr.filter(function(e){
       //
      //           return e.theYadaId === yadaArr[index].id
      //         });
      //       } else {
      //         return false;
      //       }
       //
      //   return
      //  }



       /*******************************
       * scroll yada left and right
       ********************************/
       $scope.scrollLeft = function() {
          YadaExtService.scrollLeft();
          $scope.yadas = YadaExtService.updateYadas();
          $scope.yadaScrollIndex = YadaExtService.getIndex();
       }

       $scope.scrollRight = function() {
         YadaExtService.scrollRight();
         $scope.yadas = YadaExtService.updateYadas();
         $scope.yadaScrollIndex = YadaExtService.getIndex();
       }

       /*******************************
       * up and down voting
       ********************************/
       $scope.upIt = function (yada) {
           YadaExtService.upKarma(yada, function() {


                 $scope.yadas = YadaExtService.updateYadas();
                 $scope.userObj = UserExtService.getUser();

                 $scope.yadaUserJoinList = UserExtService.getYadaUserJoinList();
                 console.log("up", $scope.userVotingState);

                 $location.path("/");
           });

       }
       $scope.downIt = function (yada) {
           YadaExtService.downKarma(yada, function() {


              $scope.yadas = YadaExtService.updateYadas();
              $scope.userObj = UserExtService.getUser();

              $scope.yadaUserJoinList = UserExtService.getYadaUserJoinList();
              console.log("down", $scope.userVotingState);

               $location.path("/");
           });

       }

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

  var ext = angular.module('YadaExtension', ['ngRoute', 'ngCookies', 'angular-storage', 'angular-jwt'])

  //Router
  .config(['$routeProvider', function ($routeProvider) {

    $routeProvider.when('/', {
      templateUrl: '/home.html',
      controller: 'YadaExtController'
    }).when('/log-in', {
      templateUrl: '/log-in.html',
      controller: 'LoginExtController'
    }).when('/editor', {
      templateUrl: '/editor.html',
      controller: 'EditorExtController'
    });
  }]).run(['$rootScope', '$location', 'UserExtService', function ($rootScope, $location, UserExtService) {
    // sends a request to server to check session info
    // records session info to persist between refreshes
    $rootScope.userObj = UserExtService.checkLogStatus();

    //stores current url in rootScope
    $rootScope.extUrl = document.referrer;

    //defines entrance animation slide()
    var mainBox = document.getElementById('mainBox');
    var slide = function slide() {
      TweenMax.from(mainBox, 0.7, { left: '150%', autoAlpha: 0 });
    };
    // defines animation wrapper element for the main-container
    Draggable.create("#mainBox", { type: "x,y", throwProps: "true", edgeResistance: 0.35 });

    //default variables to send message to chrome ext (nothing current happening)
    var chromeId = "oceicbhfpbbeomhchbhoklfhnigpolle";
    var message = { greeting: "hello from angular land" };
    chrome.runtime.sendMessage(chromeId, message);

    slide();

    // callback and listener for enableBrowserAction()
    // pretty much just used for animation at this point.
    var fromExt = function fromExt(msg, sender) {
      slide();
    };
    chrome.runtime.onMessage.addListener(fromExt);
  }]);

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
      let userId = undefined;
      let yujList = [];
      let logStatus = {status: false};
      let votingStatus = {status: 3};

      return {

        /*******************************
        * Set user
        ********************************/
        setUser(user) {

          $http({
            url: 'http://localhost:8080/login',
            method: 'POST',
            data: user
          }).then(function(response) {
            console.log("user obj login", response.data);
            user = response.data;
            angular.copy(user, userObj);
            let log = {status: true};
            angular.copy(log, logStatus);

            $location.path('/');
            return user;
          })
          return
        },

        checkLogStatus() {
          $http({
            url: 'http://localhost:8080/logStatus',
            method: 'GET'
          }).then(function(response) {
            console.log("user obj check status", response.data);

            let user = response.data

            angular.copy(user, userObj);
            angular.copy(user.id, userId);
            let log = {status: true};
            angular.copy(log, logStatus);

            $location.path('/');
            return user
          })
          return
        },


        /*******************************
        * Return log status
        ********************************/
        getLogStatus() {

          return logStatus;
        },

        /*******************************
        * Return current user
        ********************************/
        getUser() {

          return userObj;
        },

        /*******************************
        * Return yada user join list
        ********************************/
        getYadaUserJoinList() {
          $http({
            url: 'http://localhost:8080/yadaUserJoinList',
            method: 'GET'
          }).then(function(response) {
            console.log("yuj-list get", response.data);

            let yuj = response.data
            angular.copy(yuj, yujList);
            let log = {status: true};
            angular.copy(log, logStatus);

            $location.path('/');
            return yuj
          })
          return yujList
        },
        /*******************************
        * Return yada user join list
        ********************************/
        getUserVotingState(id) {
            statusUrl = `http://localhost:8080/votingStatus${id}`
          $http({
            url: statusUrl,
            method: 'GET'
          }).then(function success(response) {
            console.log("votingStatus", response.data);

            let status = response.data
            angular.copy(status, votingStatus);

            $location.path('/');
            return status
          }, function error(response){

            console.log("uvs error", response);
            return votingStatus
            $location.path('/');
          });

          return votingStatus;
        },

        /*******************************
        * clear session and user info
        * reset log status and redirect to ext home
        ********************************/
        clearSession() {
          $http({
            url: 'http://localhost:8080/logout',
            method: 'POST',
            data: {
              user: userObj,
            }
          }).then(function() {

            user = {};
            id = undefined;
            let log = {status: false};

            angular.copy(id, userId);
            angular.copy(user, userObj);
            angular.copy(log, logStatus);

            $location.path('/');
            return user
          });
          return
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

  ext.factory('YadaExtService', ['$http','$rootScope','$location', 'UserExtService', function($http, $rootScope, $location, UserExtService){

      let yadas = [];
      let yadaIndex = 0;
      let scrapes = [];
      let yadaId = 0;
      let blankYada = [{
        content: "You should write a Yada for this article.",
        user: {
          username: "Noone, but it could be you!"
        },
        karma: 0
     }];

      return {

        /*******************************
        * Grab yadas from DB
        ********************************/
        getYadas(extUrl) {

          let currentUrl = 'http://localhost:8080/lemmieSeeTheYadas?url=' + extUrl;
          $http({
              url: currentUrl,
              method: 'GET'
            }).then(function success(response){
              console.log("get", response.data);
              currentYadas = response.data;
              if(currentYadas === '') {
                console.log("blank array on getYadas");
                angular.copy(blankYada, yadas);
                return yadas
              } else {
                  let yid = currentYadas[yadaIndex].id;
                  angular.copy(yid, yadaId);
                  angular.copy(currentYadas, yadas);
                  return yadas
              }

            }, function error(response){
              console.log("error on getYadas");
              angular.copy(blankYada, yadas);
            });
            console.log(yadas);
            return yadas;
        },

        /*******************************
        * Grab scraped text by sending current tabs url
        ********************************/
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

        /*******************************
        * up voting yada
        ********************************/
        upKarma(yada, callback) {

            $http({
              url: 'http://localhost:8080/upVoteExt',
              method: 'POST',
              data: yada
            }).then(function(response){
              console.log(response.data);

              let link = response.data;

              angular.copy(link.yadaList, yadas);

            }).then(callback)
        },
        /*******************************
        * down voting yada
        ********************************/
        downKarma(yada, callback) {

          $http({
            url: 'http://localhost:8080/downVoteExt',
            method: 'POST',
            data: yada
          }).then(function(response){
            console.log(response.data);

            let link = response.data;

            angular.copy(link.yadaList, yadas);

          }).then(callback)
        },
        /*******************************
        * update w/out new server request
        ********************************/
        updateYadas() {
          console.log("updating");
          return yadas;
        },

        /*******************************
        * posts new yadas from editor
        ********************************/
        sendYada(extUrl, yadaText, callback) {

          $http({
            url: "http://localhost:8080/addYada",
            method: 'POST',
            data: {
              yada: {content: `${yadaText}`},
              link: {url: `${extUrl}`}
            }
          }).then(function success(response) {
            console.log("success", response);
            callback('success');
          }, function error(response){
            console.log("error", response);
            callback('error');
          });

        },

        scrollLeft() {
           if (yadaIndex <= 0) {
             yadaIndex = yadas.length -1;
           } else {
             yadaIndex --;
           }
        },

        scrollRight() {
           if (yadaIndex >= yadas.length -1) {
             yadaIndex = 0;
           } else {
             yadaIndex ++;
           }
        },

        getIndex() {
          return yadaIndex;
        },
        getId() {
          return yadaId;
        }


      }
  }]);
}

},{}]},{},[5])