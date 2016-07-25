var x = false;
disableBrowserAction();

function disableBrowserAction(){
    chrome.browserAction.setIcon({path:"assets/inactive.png"});


    chrome.tabs.executeScript(null, {file: "js/togglecontent.js"});
}

function enableBrowserAction(tab){
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
    chrome.tabs.sendMessage(tabs[0].id, {greeting: `${tab.url}`}, function(response) {
      console.log(response);
      });
    });

    chrome.browserAction.setIcon({path:"assets/active.png"});

    console.log('Creating ' + tab.url + ' yada!');
    chrome.tabs.executeScript(null, {file: "js/content.js"});

}

function updateState(tab){
    if(x==false){
        x=true;
        enableBrowserAction(tab);
    }else{
        x=false;
        disableBrowserAction();
    }
}

chrome.browserAction.onClicked.addListener(updateState);
chrome.tabs.onActivated.addListener(disableBrowserAction);
