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

       $scope.userVotingState = UserExtService.getUserVotingState();
      //  $filter('filter')($scope.userObj.yadaUserJoinList, {theYadaId: $scope.yadas[$scope.yadaScrollIndex].id} );
       $scope.downvoted = false;


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
          $scope.yadas = YadaExtService.updateYadas();
          $scope.yadaScrollIndex = YadaExtService.getIndex();
       }

       $scope.scrollRight = function() {
         YadaExtService.scrollRight();
         $scope.yadas = YadaExtService.updateYadas();
         $scope.yadaScrollIndex = YadaExtService.getIndex();
       }

       /*******************************
       * up and down voting
       ********************************/
       $scope.upIt = function (yada) {
           YadaExtService.upKarma(yada, function() {

                 UserExtService.checkLogStatus();

                 $scope.yadas = YadaExtService.updateYadas();
                 $scope.userObj = UserExtService.getUser();

                 $scope.upvoted = $scope.userObj.yadaUserJoinList[$scope.yadaScrollIndex].upvoted;
                 console.log("up", $scope.upvoted);

                 $location.path("/");
           });

       }
       $scope.downIt = function (yada) {
           YadaExtService.downKarma(yada, function() {

              UserExtService.checkLogStatus();

              $scope.yadas = YadaExtService.updateYadas();
              $scope.userObj = UserExtService.getUser();

              $scope.downvoted = $scope.userObj.yadaUserJoinList[$scope.yadaScrollIndex].downvoted;
              console.log("down", $scope.downvoted);

               $location.path("/");
           });

       }

  }]);
}
