/*******************************
* Login Controller
* display user information from service
********************************/


module.exports = function(app) {

  app.controller('LoginController', ['$scope', 'auth', 'store', '$location','UserService', function($scope, auth, store, $location, UserService){
    $scope.username = '';
    $scope.userObj = UserService.getUser();

    // Google Log in
    function onLoginSuccess(profile, token) {
        $scope.message.text = '';
        store.set('profile', profile);
        store.set('token', token);
        $location.path('/');
        $scope.loading = false;
        UserService.setUser({
          nickname: profile.nickname,
          name: profile.name,
          email: profile.email
        })
      }
      function onLoginFailed() {
        $scope.message.text = 'invalid credentials';
        $scope.loading = false;
      }


    /*******************************
    * login
    * TODO: keep track of login state
    ********************************/
    $scope.googleLogin = function () {
        $scope.message = 'loading...';
        $scope.loading = true;

        auth.signin({
          popup: true,
          connection: 'google-oauth2',
          scope: 'openid name email'
        }, onLoginSuccess, onLoginFailed);
      };


    /*******************************
    * logout
    ********************************/
    $scope.logout = function() {
      //clear session
      // $auth.logout();
      UserService.clearSession();
      console.log("loggin out");
    }




  }])
}
