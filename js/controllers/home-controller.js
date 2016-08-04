/*******************************
* Home Controller
*

********************************/

module.exports = function(app) {

  app.controller('HomeController', ['$scope', '$location', 'YadaService', function($scope, $location, YadaService){

    $scope.isCollapsed = false;

    /*******************************
    * show yada
    ********************************/
    $scope.showMeYada = function() {
      console.log("toggs");
      $scope.isCollapsed = !$scope.isCollapsed;
    }

    /*******************************
    * grab the yadas for the ng-repeat in home.html
    *********************************/

    $scope.yadas = YadaService.getTopYadas();
    $scope.searchString = "";
    $scope.colors = ['blue','red', 'green'];


    /*******************************
    * up and down voting
    ********************************/
    $scope.upIt = function (yada) {
        YadaService.upKarma(yada, function() {
              console.log("callback");
              $scope.yadas = YadaService.updateYadas();
              $location.path("/");
        });

    }
    $scope.downIt = function (yada) {
        YadaService.downKarma(yada, function() {
            console.log("callback");
            $scope.yadas = YadaService.updateYadas();
            $location.path("/");
        });

    }
    /*******************************
    * search
    ********************************/
    $scope.search = function(query) {
        console.log(query);
        YadaService.searchYadas(query, function() {
          $scope.yadas = YadaService.updateYadas();
          $scope.searchString = "";
          $location.path("/");
        });
    }
    /*******************************
    * filter results
    ********************************/
    $scope.hot = function() {
        // might want to refactor
        // add button highlighting by toggling active classes
        // $scope.yadas = YadaService.filter('hot');
        $scope.yadas = YadaService.getTopYadas();
    }
    $scope.controversial = function() {
        $scope.yadas = YadaService.filter('controversial');
    }
    $scope.new = function() {
        $scope.yadas = YadaService.filter('new');
    }
    $scope.top = function() {
        $scope.yadas = YadaService.filter('top');
    }


  }])
}
