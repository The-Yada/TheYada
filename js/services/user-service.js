/*******************************
* User Service
* stores user
********************************/


module.exports = function(app) {

  app.factory('UserService', ['$http', '$location', function($http, $location) {

      let user = {};
      let logStatus = {status: false};

      return {
        getUser() {

        },
        clearSession() {

        },
      }


  }])
}
