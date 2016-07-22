/*******************************
* Nav Controller
*
********************************/

module.exports = function(app) {

  app.controller('NavController', ['$scope', 'UserService', function($scope, UserService){

    /*******************************
    * menu collapse
    *********************************/
    // $scope.logStatus = UserService.getLogStatus();
    // $scope.user = UserService.getUser();
    $scope.isCollapsed = false;

    function signOut() {
      GoogleAuth.signOut()
      var auth2 = gapi.auth2.getAuthInstance();
      auth2.signOut().then(function () {
        console.log('User signed out.');
      });
    }





  }])
}
