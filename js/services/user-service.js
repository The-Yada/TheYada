/*******************************
* User Service
* stores user
********************************/


module.exports = function(app) {

  app.factory('UserService', ['$http', 'auth', '$location', function($http, auth, $location) {

      let userObj = {};
      let logStatus = {status: false};

      return {

        /*******************************
        * add user
        ********************************/
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
            url: 'http://localhost:8080/logout',
            method: 'POST',
          })
          .then(function(response) {
            console.log("and then", response);
            user = {};
            let log = {status: false};

            angular.copy(user, userObj);
            angular.copy(log, logStatus);
            // location.href = 'https://theyada.auth0.com/v2/logout?federated';
            $location.path('/');
          });

        },
      }


  }])
}
