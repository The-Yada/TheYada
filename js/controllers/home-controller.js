/*******************************
* Home Controller
*
********************************/

module.exports = function(app) {

  app.controller('HomeController', ['$scope', 'YadaService', function($scope, YadaService){

    /*******************************
    * grab the yadas for the ng-repeat in home.html
    *********************************/
    // YadaService.getTopYadas();
    $scope.topYadas = YadaService.getTopYadas();


    $scope.upIt = function (yada) {
        YadaService.upKarma(yada, function(response) {
              $scope.topYadas = YadaService.getTopYadas();
              return $scope.topYadas;
        });

    }
    $scope.downIt = function (yada) {
        YadaService.downKarma(yada, function(response) {
            $scope.topYadas = YadaService.getTopYadas();
            return $scope.topYadas;
        });

    }


  }])
}
