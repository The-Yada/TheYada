/*******************************
* Home Controller
*
********************************/

module.exports = function(app) {

  app.controller('HomeController', ['$scope', 'YadaService', function($scope, YadaService){

    /*******************************
    * grab the yadas for the ng-repeat in home.html
    *********************************/
    $scope.topYadas = YadaService.getTopYadas();


    $scope.upIt = function (yada) {
        YadaService.upKarma(yada);
    }
    $scope.downIt = function (yada) {
        YadaService.downKarma(yada);
    }


  }])
}
