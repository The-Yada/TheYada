/*******************************
* Nav Controller
*
********************************/

module.exports = function(app) {

  app.controller('NavController', ['$scope', '$location', 'YadaService', 'UserService', function($scope, $location, YadaService, UserService){

    /*******************************
    * menu collapse
    *********************************/
    $scope.logStatus = UserService.getLogStatus();
    // $scope.user = UserService.getUser();
    // $scope.isCollapsed = false;

    $scope.home = function() {
      YadaService.getTopYadas();
    }


  }])
}
