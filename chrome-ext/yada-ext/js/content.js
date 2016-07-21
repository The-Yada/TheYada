function showUrl(message, sender, callback) {
  console.log("getting message" + message);
  console.log("getting sender" + sender);
  return message;
}

chrome.runtime.onMessage.addListener(showUrl)

var f = document.createElement('iframe'),
    frameStyleElement = document.createElement('link'),
    frameStyleHref = chrome.runtime.getURL('css/frame.css');

// Inject the frame styles programmatically in order to avoid flickering:
frameStyleElement.href = frameStyleHref;
frameStyleElement.rel = 'stylesheet';
document.querySelector('head').appendChild(frameStyleElement);

// Configure the frame:
f.id = 'frame';
f.src = chrome.extension.getURL('yada.html');




document.body.appendChild(f); // Append to body, for example.
console.log('Creating yada!');
