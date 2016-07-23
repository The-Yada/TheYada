/*******************************
* Login Controller
* display user information from service
********************************/


module.exports = function(app) {

  app.controller('LoginController', ['$scope', 'UserService', function($scope, UserService){
    $scope.username = '';
    $scope.userObj = UserService.getUser();




    $scope.isAuthenticated = function() {

      };

      $scope.login = function() {
        //start session
        //block user input *ADD* condition if user has been created
        console.log($scope.username);
        if ($scope.username === '' || $scope.password === '') {
          console.log("enter your shit right", $scope.username);
          return
        } else {
            // check to see if user exits
            UserService.logUser(function(response){

                user = response.data.filter(function(e){
                  return e.username === $scope.username && e.password === $scope.password;
                })
                $scope.username = '';
                $scope.password = '';

                if (user.length === 1) {
                  // set user session, probs change *login* link to *logout*

                  return user[0].info;
                } else {
                  // create new user
                  console.log("create new user");

                  //** need server and db ** set username and password

                  // VolunteerService.setVol(username, password)
                  return
                }
            });

        }
      }


    $scope.logout = function() {
      //clear session

    }




  }])
}
