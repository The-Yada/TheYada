/*******************************
* Home Controller
*
********************************/

module.exports = function(app) {

  app.controller('HomeController', ['$scope', '$location', 'YadaService', function($scope, $location, YadaService){

    /*******************************
    * grab the yadas for the ng-repeat in home.html
    *********************************/
    // YadaService.getTopYadas();
    $scope.topYadas = YadaService.getTopYadas();


    $scope.upIt = function (yada) {
        YadaService.upKarma(yada, function() {
              console.log("callback");
              $scope.topYadas = YadaService.updateYadas();
              $location.path("/");
        });

    }
    $scope.downIt = function (yada) {
        YadaService.downKarma(yada, function() {
            console.log("callback");
            $scope.topYadas = YadaService.updateYadas();
            $location.path("/");
        });

    }


  }])
}
