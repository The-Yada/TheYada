/*******************************
* Nav Ext Controller
*
********************************/

module.exports = function(ext) {

  ext.controller('NavExtController', ['$scope', '$rootScope','UserExtService', function($scope, $rootScope, UserExtService){


    /*******************************
    * menu collapse
    *********************************/
    $scope.logStatus = UserExtService.getLogStatus();
    $scope.isCollapsed = false;

    $scope.toWebsite = function() {
      let win = window.open("http://localhost:8080", '_blank');
      win.focus();
    }

    $scope.logout = function() {
      //clear session
      UserExtService.clearSession();
    }

  }]);
}
