var x = false;


function disableBrowserAction(tab){
    chrome.browserAction.setIcon({path:"assets/inactive.png"});

    console.log('Disabling ' + tab.url + ' yada!');
    chrome.tabs.executeScript(null, {file: "js/togglecontent.js"});
}

function enableBrowserAction(tab){

    chrome.browserAction.setIcon({path:"assets/active.png"});

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
