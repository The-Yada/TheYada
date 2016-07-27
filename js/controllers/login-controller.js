/*******************************
* Login Controller
* display user information from service
********************************/


module.exports = function(app) {

  app.controller('LoginController', ['$scope', 'UserService', function($scope, UserService){
    $scope.username = '';
    $scope.userObj = UserService.getUser();

    /*******************************
    * login
    * TODO: keep track of login state
    ********************************/
      $scope.login = function() {
        //start session
        //block user input *ADD* condition if user has been created
        console.log($scope.username);
        if ($scope.username === '' || $scope.password === '') {
          console.log("enter your shit right", $scope.username);
          return
        } else {
            UserService.setUser({username: $scope.username, password: $scope.password});
            $scope.username = '';
            $scope.password = '';

        }
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
