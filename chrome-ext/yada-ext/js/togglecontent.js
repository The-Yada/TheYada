// find iframe by id and destroy it

(function() {
  let f = document.getElementById('frame');
  if (f !== null) {
    f.classList.add('hidden');
  } else {
    console.log('hellow lets do nothing');
  }
})()
