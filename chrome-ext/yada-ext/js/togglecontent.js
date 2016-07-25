// find iframe by id and destroy it
console.log(document.getElementById('frame'));
if (document.getElementById('frame') !== null) {
  document.getElementById('frame').classList.add('hidden');
} else {
  console.log('hellow');
}
