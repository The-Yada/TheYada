/*******************************
* Nav Controller
*
********************************/

module.exports = function(app) {

  app.controller('NavController', ['$scope','$location', 'YadaService', 'UserService', function($scope, $location, YadaService, UserService){

    $scope.logStatus = UserService.getLogStatus();

    $scope.isAuthenticated = function() {

    };
    /*******************************
    * menu collapse
    *********************************/

    // display user name on home page vvvvvv
          // $scope.user = UserService.getUser();

    // collpasable menu vvvvvvv
          // $scope.isCollapsed = false;

    /*******************************
    * get yadas from server for home page
    ********************************/
    $scope.home = function() {
      YadaService.getTopYadas();
    }


  }])
}
