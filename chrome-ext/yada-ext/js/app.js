
(function loadYada() {
    let extUrl = document.referrer;
    let currentUrl = 'http://localhost:8080/lemmieSeeTheYadas?url=' + extUrl;

    let request = new XMLHttpRequest();
    request.addEventListener('load', function() {

      console.log(currentUrl);
      let yada = JSON.parse(this.responseText);
      console.log(yada);
      let content = document.getElementById('content-yadaText');
      let author = document.getElementById('author-yadaText');
      let iframe = document.getElementById('yadaText')
      content.innerText = yada[0].content;
      author.innerText = yada[0].user;
      iframe.appendChild(content);
    })

    request.open('GET', currentUrl);
    request.send();
})();





function postIt() {
  let name = document.getElementById('editorName');
  let text = document.getElementById('editorText');
  let request = new XMLHttpRequest();

  request.open('POST', 'https://tiny-tiny.herokuapp.com/collections/cbgrid');
  request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  request.send(JSON.stringify({name: `${name.value}`, yada: `${text.value}`}));
}

// Add a click handler to the new button we just made.
let button = document.querySelector('button');
button.addEventListener('click', function () {
    console.log(`clicked on button`);
    postIt();
    // add the person

});
