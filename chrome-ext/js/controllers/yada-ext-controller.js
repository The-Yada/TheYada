/*******************************
* Yada Ext Controller
* display yadas from the current Url
********************************/

module.exports = function(ext) {

  ext.controller('YadaExtController', ['$scope', '$filter', '$rootScope','$location','YadaExtService', 'UserExtService', function($scope, $filter, $rootScope, $location, YadaExtService, UserExtService){
       UserExtService.checkLogStatus();

       $scope.yadaScrollIndex = YadaExtService.getIndex();
       $scope.yadas = YadaExtService.getYadas($rootScope.extUrl);
       $scope.userObj = UserExtService.checkLogStatus();
       $scope.yadaId = 0;

       $scope.yadaUserJoinList = UserExtService.getYadaUserJoinList();

       $scope.userVotingState = UserExtService.getUserVotingState($scope.yadaId);
      //  $scope.voted = false;



      //  $scope.karmaStatus = function(userJoins, yadaArr, index) {
       //
      //        if(userJoins !== null) {
      //          let arr = [];
       //
      //          yadaArr.forEach(function(yada) {
      //             console.log(yada);
      //              userJoins.forEach(function(yuj){
      //                  if (yuj.theYadaId === yada.id) {
      //                    console.log("what", yuj, yada.id);
      //                    arr.push(yuj);
      //                  }
      //              })
      //         })
       //
      //         return arr.filter(function(e){
       //
      //           return e.theYadaId === yadaArr[index].id
      //         });
      //       } else {
      //         return false;
      //       }
       //
      //   return
      //  }



       /*******************************
       * scroll yada left and right
       ********************************/
       $scope.scrollLeft = function() {
          YadaExtService.scrollLeft();
          $scope.userVotingState = UserExtService.getUserVotingState(YadaExtService.getYadaId())
          $scope.yadas = YadaExtService.updateYadas();
          $scope.yadaScrollIndex = YadaExtService.getIndex();
       }

       $scope.scrollRight = function() {
         YadaExtService.scrollRight();
         $scope.userVotingState = UserExtService.getUserVotingState(YadaExtService.getYadaId())
         $scope.yadas = YadaExtService.updateYadas();
         $scope.yadaScrollIndex = YadaExtService.getIndex();
       }

       /*******************************
       * up and down voting
       ********************************/
       $scope.upIt = function (yada) {
           YadaExtService.upKarma(yada, function() {


                 $scope.yadas = YadaExtService.updateYadas();
                 $scope.userObj = UserExtService.getUser();

                 $scope.userVotingState = UserExtService.getUserVotingState(YadaExtService.getYadaId())
                 $scope.yadaUserJoinList = UserExtService.getYadaUserJoinList();
                 console.log("up", $scope.userVotingState);

                 $location.path("/");
           });

       }
       $scope.downIt = function (yada) {
           YadaExtService.downKarma(yada, function() {


              $scope.yadas = YadaExtService.updateYadas();
              $scope.userObj = UserExtService.getUser();

              $scope.userVotingState = UserExtService.getUserVotingState(YadaExtService.getYadaId())
              $scope.yadaUserJoinList = UserExtService.getYadaUserJoinList();
              console.log("down", $scope.userVotingState);

               $location.path("/");
           });

       }

  }]);
}
