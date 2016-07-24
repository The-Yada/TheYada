/*******************************
* Login Extension Controller
* display user information from service
********************************/


module.exports = function(ext) {

  ext.controller('LoginExtController', ['$scope', 'UserExtService', function($scope, UserExtService){
    $scope.username = '';
    $scope.userObj = UserExtService.getUser();


      $scope.login = function() {
        //start session
        //block user input *ADD* condition if user has been created
        console.log($scope.username);
        if ($scope.username === '' || $scope.password === '') {
          console.log("enter your password right", $scope.username);
          return
        } else {
            UserExtService.setUser({username: $scope.username, password: $scope.password});
            $scope.username = '';
            $scope.password = '';
        }
      }


    $scope.logout = function() {
      //clear session
      UserExtService.clearSession();
    }




  }])
}
