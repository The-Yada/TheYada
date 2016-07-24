/*******************************
* The Yada Chrome Extension
* Date: 7-18-2016
*
********************************/



(function() {
"use strict"

  let ext = angular.module('YadaExtension', ['ngRoute'])

    //Router
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider
          .when('/', {
            templateUrl: '/home.html',
            controller: 'YadaExtController',
          })
          .when('/log-in', {
            templateUrl: '/log-in.html',
            controller: 'LoginExtController',
          })
          .when('/log-out', {
            templateUrl: '/log-out.html',
            controller: 'LoginExtController',
          })
          .when('/editor', {
            templateUrl: '/editor.html',
            controller: 'EditorExtController',
          })
    }])
    .run(function($rootScope) {
      $rootScope.extUrl = document.referrer;
    });


      // Services
      require('./services/user-ext-service')(ext);
      require('./services/yada-ext-service')(ext);


      // Controllers
      require('./controllers/nav-ext-controller')(ext);
      require('./controllers/login-ext-controller')(ext);
      require('./controllers/yada-ext-controller')(ext);
      require('./controllers/editor-ext-controller')(ext);


      // Filters

      // Directives


})()



// (function loadYada() {
//     let extUrl = document.referrer;
//     let currentUrl = 'http://localhost:8080/lemmieSeeTheYadas?url=' + extUrl;
//
//     let request = new XMLHttpRequest();
//     request.addEventListener('load', function() {
//
//       console.log(currentUrl);
//       let yada = JSON.parse(this.responseText);
//       console.log(yada);
//       let content = document.getElementById('content-yadaText');
//       let author = document.getElementById('author-yadaText');
//       let iframe = document.getElementById('yadaText')
//       content.innerText = yada[0].content;
//       author.innerText = yada[0].user;
//       iframe.appendChild(content);
//     })
//
//     request.open('GET', currentUrl);
//     request.send();
// })();

// function scrapeIt() {
//   let extUrl = document.referrer;
//   let scrapeUrl = 'http://localhost:8080/lemmieYada?url=' + extUrl;
//
//   let request = new XMLHttpRequest();
//   request.addEventListener('load', function() {
//
//     let scrapeBox = document.getElementById('yadaScrape');
//     let scrapedText = JSON.parse(this.responseText);
//     scrapedText.forEach(function(e){
//       scrapeBox.innerText += e;
//     })
//   })
//
//   request.open('GET', scrapeUrl);
//   request.send();
// }
//
// let scrapeButton = document.querySelector('#scrapeIt');
// scrapeButton.addEventListener('click', function () {
//     console.log(`clicked on scrape button`);
//     scrapeIt();
// });


// document.getElementById('login').addEventListener('click', function() {
//   let username = document.getElementById('userField');
//   let password = document.getElementById('passField');
//   let request = new XMLHttpRequest();
//
//   request.open('POST', 'http://localhost:8080/login');
//   request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
//   request.send(JSON.stringify({username:`${username.value}`, password:`${password.value}`}));
//
//   username.value = '';
//   password.value = '';
//
// });


// function postIt() {
//   console.log('trying to post');
//
//   let extUrl = document.referrer;
//
//   let text = document.getElementById('editorText');
//   let request = new XMLHttpRequest();
//
//   request.open('POST', 'http://localhost:8080/addYada');
//   // request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
//   request.send({content:`${text.value}`, url:`${extUrl}`});
//   text.value = '';
//
// }
//
// // Add a click handler to the new button we just made.
// let postButton = document.getElementById('postIt');
// postButton.addEventListener('click', function () {
//     console.log(`clicked on post button`);
//     postIt();
//     // add the person
//
// });
