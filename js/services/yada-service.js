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
              url: 'http://www.theyada.us/theYadaList',
              method: 'GET'
            }).then(function(response){
              yadas = response.data;
              angular.copy(yadas, topYadas);
            })
            console.log("initial get", topYadas);
            return topYadas;
        },
        /*******************************
        * up voting yada
        ********************************/
        upKarma(yada, callback) {

            $http({
              url: 'http://www.theyada.us/upVote',
              method: 'POST',
              data: yada
            }).then(function(response){
              console.log("up vote update", response.data);
              yadas = response.data;

              angular.copy(yadas, topYadas);
              // callback();
            }).then(callback)
        },
        /*******************************
        * down voting yada
        ********************************/
        downKarma(yada, callback) {

          $http({
            url: 'http:/www.theyada.us/downVote',
            method: 'POST',
            data: yada
          }).then(function(response){
            console.log("down vote update", response.data);
            yadas = response.data;

            angular.copy(yadas, topYadas);
            // callback();
          }).then(callback)
        },
        /*******************************
        * update w/out new server request
        ********************************/
        updateYadas() {
          console.log("updating");
          return topYadas;
        },

        /*******************************
        * search request
        ********************************/
        searchYadas(searchString, callback) {

            // Default is titleSearch
            // TODO: searching by content and author
              // let yadaSearchUrl = `/searchYadas?searchInput=${searchString}`;
              // ** authors returns hashmap of username and arraylist of yadas
              // let authorSearchUrl = `/searchAuthors?searchInput=${searchString}`;

            let titleSearchUrl = `http://www.theyada.us/searchTitles?searchInput=${searchString}`;
            $http({
                url: titleSearchUrl,
                method: 'GET'
              }).then(function(response){

                yadas = response.data;
                console.log("searching", yadas);
                angular.copy(yadas, topYadas);
              }).then(callback)

          return topYadas;
        },

        /*******************************
        * filter requests
        ********************************/
        filter(sortStyle) {
          let filterUrl = `http://www.theyada.us/${sortStyle}Links`;

          $http({
              url: filterUrl,
              method: 'GET'
            }).then(function(response){

              yadas = response.data;
              console.log("filtering", yadas);
              angular.copy(yadas, topYadas);
            })

            return topYadas;
        }

      }


  }])
}
