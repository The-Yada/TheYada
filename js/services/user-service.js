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
        setUser(userObj) {
          $http({
            url: '/user',
            method: 'POST',
            data: {
              user: userObj,
            }
          })
        },

        //when logging in
        logUser(callback) {
          $http({
            url: '/user',
            method: 'GET',
          }).then(function(response){

            let user = callback(response);
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
          user = {};
          let log = {status: false};

          angular.copy(user, vol);
          angular.copy(log, logStatus);

          $location.path('/');
        },
      }


  }])
}
