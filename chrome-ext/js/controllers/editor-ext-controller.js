/*******************************
* Editor Ext Controller
*
********************************/

module.exports = function(ext) {

  ext.controller('EditorExtController', ['$scope', '$rootScope', '$location', 'YadaExtService', 'UserExtService', function($scope, $rootScope, $location, YadaExtService, UserExtService){


    $scope.scrapedText = YadaExtService.scrapeIt($rootScope.extUrl);
    $scope.editorText = '';

    /*******************************
    * post a yada
    ********************************/
    $scope.postIt = function () {
      YadaExtService.sendYada($rootScope.extUrl, $scope.editorText, function(response) {
        if (response === "success") {
          $scope.editorText = '';
        } else {
          $scope.editorText = 'sorry you have already written a yada for this article';
        }
        
        $location.path('/');
      });
    };

  }]);
}
