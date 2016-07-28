/*******************************
* Yada Ext Controller
* display yadas from the current Url
********************************/

module.exports = function(ext) {

  ext.controller('YadaExtController', ['$scope', '$rootScope','$location','YadaExtService', function($scope, $rootScope, $location, YadaExtService){

       $scope.yadaScrollIndex = 0;
       $scope.yadas = YadaExtService.getYadas($rootScope.extUrl);

       /*******************************
       * scroll yada left and right
       ********************************/
       $scope.scrollLeft = function() {
         if ($scope.yadaScrollIndex <= 0) {
           $scope.yadaScrollIndex = $scope.yadas.length -1;
         } else {
           $scope.yadaScrollIndex --;
         }
       }
       $scope.scrollRight = function() {
         if ($scope.yadaScrollIndex >= $scope.yadas.length -1) {
           $scope.yadaScrollIndex = 0;
         } else {
           $scope.yadaScrollIndex ++;
         }
       }

       /*******************************
       * up and down voting
       ********************************/
       $scope.upIt = function (yada) {
           YadaExtService.upKarma(yada, function() {

                 $scope.yadas = YadaExtService.updateYadas();
                 $location.path("/");
           });

       }
       $scope.downIt = function (yada) {
           YadaExtService.downKarma(yada, function() {

               $scope.yadas = YadaExtService.updateYadas();
               $location.path("/");
           });

       }

  }]);
}
