var x = false;
// disableBrowserAction();

function disableBrowserAction(tab){
    chrome.browserAction.setIcon({path:"assets/inactive.png"});

    console.log('toggling ' + tab.url);

    chrome.tabs.executeScript(null, {file: "js/togglecontent.js"});
}

function enableBrowserAction(tab){
    chrome.browserAction.setIcon({path:"assets/active.png"});

    console.log('Creating ' + tab.url + ' yada!');
    chrome.tabs.executeScript(null, {file: "js/content.js"});

    chrome.tabs.sendMessage(1, "hello from the bG");
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
