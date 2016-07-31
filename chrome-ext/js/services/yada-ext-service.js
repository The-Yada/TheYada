/*******************************
* Yada Ext Service
* grabs yadas from the current Url
********************************/

module.exports = function(ext) {

  ext.factory('YadaExtService', ['$http','$rootScope','$location', function($http, $rootScope, $location){

      let yadas = [];
      let scrapes = [];
      let blankYada = [{
        content: "You should write a Yada for this article.",
        user: {
          username: "Noone, but it could be you!"
        },
        karma: 0
     }];

      return {

        /*******************************
        * Grab yadas from DB
        ********************************/
        getYadas(extUrl) {

          let currentUrl = 'http://localhost:8080/lemmieSeeTheYadas?url=' + extUrl;
          $http({
              url: currentUrl,
              method: 'GET'
            }).then(function success(response){

              currentYadas = response.data;
              if(currentYadas === '') {
                console.log("blank array on getYadas");
                angular.copy(blankYada, yadas);
              } else {
                  angular.copy(currentYadas, yadas);
              }

            }, function error(response){
              console.log("error on getYadas");
              angular.copy(blankYada, yadas);
            });
            console.log(yadas);
            return yadas;
        },

        /*******************************
        * Grab scraped text by sending current tabs url
        ********************************/
        scrapeIt(extUrl) {

          let scrapeUrl = 'http://localhost:8080/lemmieYada?url=' + extUrl;
          $http({
              url: scrapeUrl,
              method: 'GET'
            }).then(function(response){
              currentScrapes = response.data;
              angular.copy(currentScrapes, scrapes);
            })
            console.log(scrapes);
            return scrapes;
        },

        /*******************************
        * up voting yada
        ********************************/
        upKarma(yada, callback) {

            $http({
              url: 'http://localhost:8080/upVoteExt',
              method: 'POST',
              data: yada
            }).then(function(response){
              console.log(response.data);

              let link = response.data;

              angular.copy(link.yadaList, yadas);

            }).then(callback)
        },
        /*******************************
        * down voting yada
        ********************************/
        downKarma(yada, callback) {

          $http({
            url: 'http://localhost:8080/downVoteExt',
            method: 'POST',
            data: yada
          }).then(function(response){
            console.log(response.data);

            let link = response.data;

            angular.copy(link.yadaList, yadas);

          }).then(callback)
        },
        /*******************************
        * update w/out new server request
        ********************************/
        updateYadas() {
          console.log("updating");
          return yadas;
        },

        /*******************************
        * posts new yadas from editor
        ********************************/
        sendYada(extUrl, yadaText, callback) {

          $http({
            url: "http://localhost:8080/addYada",
            method: 'POST',
            data: {
              yada: {content: `${yadaText}`},
              link: {url: `${extUrl}`}
            }
          }).then(function success(response) {
            console.log("success", response);
            callback('success');
          }, function error(response){
            console.log("error", response);
            callback('error');
          });

        }

      }
  }]);
}
