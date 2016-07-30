/*******************************
* Nav Controller
*
********************************/

module.exports = function(app) {

  app.controller('NavController', ['$scope','$location', 'YadaService', 'UserService', function($scope, $location, YadaService, UserService){

    $scope.user = UserService.getUser();
    $scope.logStatus = UserService.getLogStatus();


    /*******************************
    * get yadas from server for home page
    ********************************/
    $scope.home = function() {
      YadaService.getTopYadas();
    }


  }])
}
