/*******************************
* Yada Ext Controller
* display yadas from the current Url
********************************/

module.exports = function(ext) {

  ext.controller('YadaExtController', ['$scope', '$rootScope','YadaExtService', function($scope, $rootScope, YadaExtService){

      
      $scope.yadas = YadaExtService.getYadas($rootScope.extUrl);

  }]);
}
