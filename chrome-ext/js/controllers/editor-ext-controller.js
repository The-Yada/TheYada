/*******************************
* Editor Ext Controller
*
********************************/

module.exports = function(ext) {

  ext.controller('EditorExtController', ['$scope', '$rootScope','YadaExtService', function($scope, $rootScope, YadaExtService){

    console.log("hello url", $rootScope.extUrl);
    $scope.scrapedText = YadaExtService.scrapeIt($rootScope.extUrl);
    $scope.editorText = '';

    $scope.postIt = function () {
      YadaExtService.sendYada($rootScope.extUrl, $scope.editorText);
    };

  }]);
}
