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
