/*******************************
* Login Controller
* display user information from service
********************************/


module.exports = function(app) {

  app.controller('LoginController', ['$scope', 'UserService', '$auth', function($scope, UserService, $auth){
    $scope.username = '';
    $scope.user = UserService.getUser();

    $scope.login = function() {
      // OAuth stuff
      UserService.getUser()

    }

    $scope.authenticate = function(provider) {
      $auth.authenticate(provider);
      console.log(provider);
    };



    $scope.logout = function() {
      //clear session
      UserService.clearSession()

    }




  }])
}
