/*******************************
* Yada Service
* grabs yadaList from server
********************************/


module.exports = function(app) {

  app.factory('YadaService', ['$http', '$location', function($http, $location) {

    /*******************************
      yadaList should look list:

      [
          {
              title: "",
              author: "",
              date: {},
              url: "",
              yada: "",
              karma: ""
          }
      ]
    ********************************/
      let topYadas = [];




      return {
        /*******************************
        * get yadas from server for home page
        ********************************/
        getTopYadas() {
          $http({
              url: '/yadaList',
              method: 'GET'
            }).then(function(response){
              yadas = response.data;
              angular.copy(yadas, topYadas);
            })
            console.log(topYadas);
            return topYadas;
        }
      }


  }])
}
