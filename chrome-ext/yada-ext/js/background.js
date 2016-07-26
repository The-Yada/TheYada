var x = false;


function disableBrowserAction(tab){
    chrome.browserAction.setIcon({path:"assets/yada-bulb-off-browser-icon.png"});

    console.log('Disabling ' + tab.url + ' yada!');
    chrome.tabs.executeScript(null, {file: "js/togglecontent.js"});
}

function enableBrowserAction(tab){

    chrome.contentSettings.cookies.set({
      primaryPattern: "http://localhost:8080/*",
      setting: "session_only"
    });

    chrome.browserAction.setIcon({path:"assets/yada-bulb-on-browser-icon.png"});

    console.log('Enabling ' + tab.url + ' yada!');
    chrome.tabs.executeScript(null, {file: "js/content.js"});

}

function updateState(tab){
    if(x==false){
        x=true;
        enableBrowserAction(tab);
    }else{
        x=false;
        disableBrowserAction(tab);
    }
}


chrome.browserAction.onClicked.addListener(updateState);
chrome.tabs.onActivated.addListener(disableBrowserAction);
