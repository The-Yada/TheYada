(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);throw new Error("Cannot find module '"+o+"'")}var f=n[o]={exports:{}};t[o][0].call(f.exports,function(e){var n=t[o][1][e];return s(n?n:e)},f,f.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){

module.exports = function(app) {

  app.animation('.roloAnimation', [ function() {

    return {

      /*******************************
      * animations and click events
      ********************************/

        enter: function() {


          let boxes  = Array.from(document.querySelectorAll('.box'));

          let time  = 50;
          let total = boxes.length;
          let step  = 1 / total;

          // optional: delay [can also reverse by setting '-step' or '-1']
          // let delay = step * time;
          let delay = 1 / total * time;

          let w1 = 600;
          let w2 = 100;
          let y1 = w1 / 2 - w2 / 2;
          let y2 = w1 - w2 + 100;
          let z1 = -200;
          let z2 = z1 / 2;

          let values = [
            { y: y1, z: 0  },
            { y: y2, z: z2 },
            { y: y1, z: z1 },
            { y: 0,  z: z2 },
            { y: y1, z: 0  },
          ];

          TweenLite.defaultEase = Linear.easeNone;

          TweenLite.set("#rolodex", {
            perspective: 200,
            rotationY: 20,
            transformStyle: "preserve-3d"
          });

          TweenLite.set(boxes, { y: y1, z: 0 });

          let bezier = { values: values, type: "soft" };

          let timeline = boxes.map(bezierTween)
            .reduce(buildTimeline, new TimelineMax());

          let pauseTween = TweenLite.to(timeline, 1, { timeScale: 0 }).reverse();

          toggle.addEventListener("click", function() {
            controlTween.reversed(!controlTween.reversed());
          });
          pause.addEventListener("click", function() {
            controlTween.pause();
          });

          function bezierTween(box) {
            return TweenMax.to(box, time, { bezier: bezier, repeat: 3 });
          }

          function buildTimeline(tl, tween, i) {
            return tl.add(tween, i * delay);
          }

          let controlTween = new TimelineMax({repeat:-1})
          controlTween.add(timeline.tweenFromTo(50, 100));

          controlTween.eventCallback("onUpdate", adjustUI)
          progressSlider.addEventListener("input", update);


          document.getElementById("resume").onclick = function() {
            controlTween.resume();
          }

          boxes.forEach(handleClick);

          function handleClick(element, i) {
            let button = document.querySelector("#" + element.dataset.button);
            let toggleYada = document.querySelector("#toggleYada-"+ i);
            let linkTitle = document.querySelector("#linkTitle-"+ i);
            let yada = document.querySelector("#yada-"+ i);

            let tween = TweenMax.to(linkTitle, 1, {opacity: 0, height: "0px"}).reverse();
            let tween2 = TweenMax.fromTo(yada, 1, {opacity: 0, height: '0px'},{opacity: 1, height: '100px'}).reverse();
            toggleYada.addEventListener("click", function() {
              tween2.reversed(!tween.reversed()).delay(1);
              tween.reversed(!tween.reversed());
            });

            button.addEventListener("click", function() {
              tweenTo(i * step);
            });
          }



          function tweenTo(progress) {
            controlTween.pause();
            TweenLite.to(controlTween, 0.3, {progress:progress})
          }

          function update(){
              controlTween.progress(progressSlider.value).pause();
          }

          function adjustUI() {
            progressSlider.value = controlTween.progress();
          }

          $(window).scroll(function(e){
            let scrollTop = $(window).scrollTop();
            let docHeight = $(document).height();
            let winHeight = $(window).height();
            let scrollPercent = (scrollTop) / (docHeight - winHeight);
            let scrollPercentRounded = Math.round(scrollPercent*100)/100;

            timeline.progress( scrollPercent ).pause();
            progressSlider.value = scrollPercent;
          });



        }



    }

  }])
}

},{}],2:[function(require,module,exports){
/*******************************
* Home Controller
*

********************************/

module.exports = function(app) {

  app.controller('HomeController', ['$scope', '$location', 'YadaService', function($scope, $location, YadaService){

    /*******************************
    * show yada
    ********************************/
    $scope.isCollapsed = [];


    /*******************************
    * grab the yadas for the ng-repeat in home.html
    *********************************/

    $scope.yadas = YadaService.getTopYadas();
    $scope.searchString = "";
    $scope.colors = ['blue','red', 'green'];


    /*******************************
    * up and down voting
    ********************************/
    $scope.upIt = function (yada) {
        YadaService.upKarma(yada, function() {
              console.log("callback");
              $scope.yadas = YadaService.updateYadas();
              $location.path("/");
        });

    }
    $scope.downIt = function (yada) {
        YadaService.downKarma(yada, function() {
            console.log("callback");
            $scope.yadas = YadaService.updateYadas();
            $location.path("/");
        });

    }
    /*******************************
    * search
    ********************************/
    $scope.search = function(query) {
        console.log(query);
        YadaService.searchYadas(query, function() {
          $scope.yadas = YadaService.updateYadas();
          $scope.searchString = "";
          $location.path("/");
        });
    }
    /*******************************
    * filter results
    ********************************/
    $scope.hot = function() {
        // might want to refactor
        // add button highlighting by toggling active classes
        // $scope.yadas = YadaService.filter('hot');
        $scope.yadas = YadaService.getTopYadas();
    }
    $scope.controversial = function() {
        $scope.yadas = YadaService.filter('controversial');
    }
    $scope.new = function() {
        $scope.yadas = YadaService.filter('new');
    }
    $scope.top = function() {
        $scope.yadas = YadaService.filter('top');
    }


  }])
}

},{}],3:[function(require,module,exports){
/*******************************
* Login Controller
* display user information from service
********************************/


module.exports = function(app) {

  app.controller('LoginController', ['$scope', '$location','UserService', function($scope, $location, UserService){

    $scope.userObj = UserService.getUser();


    /*******************************
    * login
    ********************************/
    $scope.login = function () {
      UserService.setUser({
        username: $scope.username,
        password: $scope.password,
      })
    }


    /*******************************
    * logout
    ********************************/
    $scope.logout = function() {
      //clear session
      UserService.clearSession();
    }




  }])
}

},{}],4:[function(require,module,exports){
/*******************************
* Nav Controller
*
********************************/

module.exports = function(app) {

  app.controller('NavController', ['$scope','$location', 'YadaService', 'UserService', function($scope, $location, YadaService, UserService){

    $scope.user = UserService.getUser();
    $scope.logStatus = UserService.getLogStatus();


    /*******************************
    * get yadas from server for home page
    ********************************/
    $scope.home = function() {
      YadaService.getTopYadas();
    }


  }])
}

},{}],5:[function(require,module,exports){
'use strict';

/*******************************
* The Yada Web App
* Date: 7-18-2016
*
********************************/

(function () {
  "use strict";

  var app = angular.module('YadaWebApp', ['ngRoute', 'ngAnimate'])

  /*******************************
  * ROUTER
  *********************************/
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
  }])
  /*******************************
  * run function when app is initiated
  * could be used to check for cookies or user log status
  *********************************/
  .run(['$rootScope', '$location', 'UserService', function ($rootScope, $location, UserService) {

    UserService.checkLogStatus();
  }]);

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
},{"./animations.js":1,"./controllers/home-controller":2,"./controllers/login-controller":3,"./controllers/nav-controller":4,"./filters/search-filter.js":6,"./services/user-service":7,"./services/yada-service":8}],6:[function(require,module,exports){

module.exports = function (app) {

    app.filter('searchFor', function(){
        return function(arr, searchString){
            if(!searchString){
                return arr;
            }
            var result = [];
            searchString = searchString.toLowerCase();
            angular.forEach(arr, function(item){

                item.yadaList.forEach(function(e){

                      if(e.content.toLowerCase().indexOf(searchString) !== -1){
                        console.log(e);
                      // result.push(e);
                  }
                })
      
            });
            return result;
        };
    });
}

},{}],7:[function(require,module,exports){
/*******************************
* User Service
* stores user
********************************/


module.exports = function(app) {

  app.factory('UserService', ['$http', '$location', function($http, $location) {

      let userObj = {};
      let logStatus = {status: false};

      return {

        /*******************************
        * add user
        ********************************/
        setUser(user) {

          $http({
            url: 'http://www.theyada.us/login',
            method: 'POST',
            data: user
          }).then(function() {
            angular.copy(user, userObj);
            let log = {status: true};
            angular.copy(log, logStatus);

            $location.path('/');
          })
        },

        checkLogStatus() {
          $http({
            url: 'http://www.theyada.us/logStatus',
            method: 'GET'
          }).then(function(response) {
            console.log("user check", response.data);

            let user = response.data
            angular.copy(user, userObj);
            let log = {status: true};
            angular.copy(log, logStatus);

            $location.path('/');
          })
        },


        /*******************************
        * return log status
        ********************************/
        getLogStatus() {
            return logStatus;
        },

        /*******************************
        * current user
        ********************************/
        getUser() {

          return userObj;
        },

        /*******************************
        * clear out user information and reset status
        * redirect to homepage
        ********************************/
        clearSession() {
          $http({
            url: 'http://www.theyada.us/logout',
            method: 'POST',
          })
          .then(function(response) {
            console.log("and then", response);
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

},{}],8:[function(require,module,exports){
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
              url: 'http://www.theyada.us/theYadaList',
              method: 'GET'
            }).then(function(response){
              yadas = response.data;
              angular.copy(yadas, topYadas);
            })
            console.log("initial get", topYadas);
            return topYadas;
        },
        /*******************************
        * up voting yada
        ********************************/
        upKarma(yada, callback) {

            $http({
              url: 'http://www.theyada.us/upVote',
              method: 'POST',
              data: yada
            }).then(function(response){
              console.log("up vote update", response.data);
              yadas = response.data;

              angular.copy(yadas, topYadas);
              // callback();
            }).then(callback)
        },
        /*******************************
        * down voting yada
        ********************************/
        downKarma(yada, callback) {

          $http({
            url: 'http:/www.theyada.us/downVote',
            method: 'POST',
            data: yada
          }).then(function(response){
            console.log("down vote update", response.data);
            yadas = response.data;

            angular.copy(yadas, topYadas);
            // callback();
          }).then(callback)
        },
        /*******************************
        * update w/out new server request
        ********************************/
        updateYadas() {
          console.log("updating");
          return topYadas;
        },

        /*******************************
        * search request
        ********************************/
        searchYadas(searchString, callback) {

            // Default is titleSearch
            // TODO: searching by content and author
              // let yadaSearchUrl = `/searchYadas?searchInput=${searchString}`;
              // ** authors returns hashmap of username and arraylist of yadas
              // let authorSearchUrl = `/searchAuthors?searchInput=${searchString}`;

            let titleSearchUrl = `http://www.theyada.us/searchTitles?searchInput=${searchString}`;
            $http({
                url: titleSearchUrl,
                method: 'GET'
              }).then(function(response){

                yadas = response.data;
                console.log("searching", yadas);
                angular.copy(yadas, topYadas);
              }).then(callback)

          return topYadas;
        },

        /*******************************
        * filter requests
        ********************************/
        filter(sortStyle) {
          let filterUrl = `http://www.theyada.us/${sortStyle}Links`;

          $http({
              url: filterUrl,
              method: 'GET'
            }).then(function(response){

              yadas = response.data;
              console.log("filtering", yadas);
              angular.copy(yadas, topYadas);
            })

            return topYadas;
        }

      }


  }])
}

},{}]},{},[5])