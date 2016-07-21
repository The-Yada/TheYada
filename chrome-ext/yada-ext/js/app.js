

var request = new XMLHttpRequest();
request.addEventListener('load', function() {
  // let url = 'http://yada.com/yada_url?' + tab.url;
  var author = JSON.parse(this.responseText);
  console.log(author);
  var name = document.createElement('SPAN');
  var iframe = document.getElementById('yadaText')
  name.innerText = author[0].name;
  iframe.appendChild(name);
})

request.open('GET', 'https://tiny-tiny.herokuapp.com/collections/cbgrid')
request.send();



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
