/*******************************
* Login Extension Controller
* display user information from service
********************************/



module.exports = function(ext) {

  ext.controller('LoginExtController', ['$scope', 'auth', 'UserExtService', function($scope, auth, UserExtService){

    $scope.username = '';
    $scope.userObj = UserExtService.getUser();

    // Google Log in
    function onLoginSuccess(profile, token) {
        $scope.message.text = '';
        store.set('profile', profile);
        store.set('token', token);
        $location.path('/');
        $scope.loading = false;
        UserExtService.setUser({
          nickname: profile.nickname,
          name: profile.name,
          email: profile.email
        })
      }
      function onLoginFailed() {
        console.log('log fail cont');
        $scope.message.text = 'invalid credentials';
        $scope.loading = false;
      }


    /*******************************
    * login
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


  }])
}
