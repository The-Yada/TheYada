/*******************************
* Yada Ext Controller
* display yadas from the current Url
********************************/

module.exports = function(ext) {

  ext.controller('YadaExtController', ['$scope', '$rootScope','YadaExtService', function($scope, $rootScope, YadaExtService){
       $scope.yadaScrollIndex = 0;
       $scope.yadas = YadaExtService.getYadas($rootScope.extUrl);

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

  }]);
}
