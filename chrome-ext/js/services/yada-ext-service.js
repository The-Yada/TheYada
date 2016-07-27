/*******************************
* Yada Ext Service
* grabs yadas from the current Url
********************************/

module.exports = function(ext) {

  ext.factory('YadaExtService', ['$http','$location', function($http, $location){

      let yadas = [];
      let scrapes = [];
      let blankYada = [{
        content: "You should write a Yada for this article.",
        user: {
          username: "Noone, but it could be you!"
        }
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
          }).then(callback)

        }

      }
  }]);
}
