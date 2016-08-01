/*******************************
* User Extension Service
* stores user
********************************/


module.exports = function(ext) {

  ext.factory('UserExtService', ['$http', '$location', function($http, $location) {

      let userObj = {};
      let yujList = [];
      let logStatus = {status: false};

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

        getUserVotingState() {
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
          return
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
            let log = {status: false};

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
