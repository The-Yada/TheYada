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
              url: 'http://localhost:8080/upVote',
              method: 'POST',
              data: yada
            }).then(function(response){
              
              let link = response.data.filter(function(link){
                  return link.url === $rootScope.extUrl;
              });
              console.log("after filter", link);

              angular.copy(link[0].yadaList, yadas);
              // callback();
            }).then(callback)
        },
        /*******************************
        * down voting yada
        ********************************/
        downKarma(yada, callback) {

          $http({
            url: 'http://localhost:8080/downVote',
            method: 'POST',
            data: yada
          }).then(function(response){
            console.log("down vote update", response.data);
            console.log("filter url", $rootScope.extUrl);
            let link = response.data.filter(function(link){
                return link.url === $rootScope.extUrl;
            });
            console.log("after filter", link);

            angular.copy(link[0].yadaList, yadas);
            // callback();
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
          }).then(callback)

        }

      }
  }]);
}
