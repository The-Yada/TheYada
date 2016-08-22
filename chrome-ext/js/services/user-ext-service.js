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
            url: 'http://www.theyada.us/login',
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
            url: 'http://www.theyada.us/logStatus',
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
            url: 'http://www.theyada.us/yadaUserJoinList',
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
            statusUrl = `http://www.theyada.us/voteStatus${id}`
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
            url: 'http://www.theyada.us/logout',
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
