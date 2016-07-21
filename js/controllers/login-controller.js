/*******************************
* Login Controller
* display user information from service
********************************/


module.exports = function(app) {

  app.controller('LoginController', ['$scope', 'UserService', function($scope, UserService){
    $scope.username = '';
    $scope.user = UserService.getUser();

    $scope.login = function() {
      // OAuth stuff
      UserService.getUser()
    }


    $scope.logout = function() {
      //clear session
      UserService.clearSession()

    }




  }])
}
