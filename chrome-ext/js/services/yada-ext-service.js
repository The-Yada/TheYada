/*******************************
* Yada Ext Service
* grabs yadas from the current Url
********************************/

module.exports = function(ext) {

  ext.factory('YadaExtService', ['$http','$location', function($http, $location){

      let yadas = [];
      let scrapes = [];

      return {

        getYadas(extUrl) {

          let currentUrl = 'http://localhost:8080/lemmieSeeTheYadas?url=' + extUrl;
          $http({
              url: currentUrl,
              method: 'GET'
            }).then(function(response){
              currentYadas = response.data;
              angular.copy(currentYadas, yadas);
            })
            console.log(yadas);
            return yadas;
        },

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

        sendYada(extUrl, yadaText, callback) {

          $http({
            url: "http://localhost:8080/addYada",
            method: 'POST',
            data: {
              yada: {content: `${yadaText}`},
              link: {url: `${extUrl}`}
            }
          }).then(callback)

        }

      }
  }]);
}
