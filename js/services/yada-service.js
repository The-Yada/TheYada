/*******************************
* Yada Service
* grabs yadaList from server
********************************/


module.exports = function(app) {


  app.factory('YadaService', ['$http', '$location', function($http, $location) {

    /*******************************
      yadaList should look list:
      [
          id: 0,
          linkScore: 0,
          numberOfYadas: 0,
          timeDiffInSeconds: 0,
          timeOfCreation:{},
          totalVotes: 0,
          url: "",
          yadaList:[{
              content: "",
              karma: 0,
              time: {},
              score: 0,
              user: "",
              link: ""
          }]
      ]
    ********************************/
      let topYadas = [];




      return {
        /*******************************
        * get yadas from server for home page
        ********************************/
        getTopYadas() {
          $http({
              url: '/theYadaList',
              method: 'GET'
            }).then(function(response){
              yadas = response.data;
              angular.copy(yadas, topYadas);
            })
            console.log("initial get", topYadas);
            return topYadas;
        },

        upKarma(yada, callback) {

            $http({
              url: '/upVote',
              method: 'POST',
              data: yada
            }).then(function(response){
              console.log("up vote update", response.data);
              yadas = response.data;

              angular.copy(yadas, topYadas);
              // callback();
            }).then(callback)
        },

        downKarma(yada, callback) {

          $http({
            url: '/downVote',
            method: 'POST',
            data: yada
          }).then(function(response){
            console.log("down vote update", response.data);
            yadas = response.data;

            angular.copy(yadas, topYadas);
            // callback();
          }).then(callback)
        },

        updateYadas() {
          console.log("updating");
          return topYadas;
        }

      }


  }])
}
