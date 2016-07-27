/*******************************
* Editor Ext Controller
*
********************************/

module.exports = function(ext) {

  ext.controller('EditorExtController', ['$scope', '$rootScope', '$location', 'YadaExtService', function($scope, $rootScope, $location, YadaExtService){

    console.log("hello url", $rootScope.extUrl);
    $scope.scrapedText = YadaExtService.scrapeIt($rootScope.extUrl);
    $scope.editorText = '';

    $scope.postIt = function () {
      YadaExtService.sendYada($rootScope.extUrl, $scope.editorText, function() {
        $scope.editorText = '';
        $location.path('/');
      });
    };

  }]);
}
