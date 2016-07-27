/*******************************
* CONTENT
* (chrome extension content file)
********************************/

(function() {

  // Conditional to help determine whether to toggle the frame or initialize it.
  if (document.getElementById('frame') === null) {
    let f = document.createElement('iframe'),
        frameStyleElement = document.createElement('link'),
        frameStyleHref = chrome.runtime.getURL('css/frame.css');

    // Inject the frame styles programmatically in order to avoid flickering:
    frameStyleElement.href = frameStyleHref;
    frameStyleElement.rel = 'stylesheet';
    document.querySelector('head').appendChild(frameStyleElement);

    // Configure the frame:
    f.id = 'frame';
    f.src = chrome.extension.getURL('yada.html');


    // append frame to body
    document.body.appendChild(f);


    console.log('Creating yada!');
  } else {
    document.getElementById('frame').classList.remove('hidden');
  }

})();
