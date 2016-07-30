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
