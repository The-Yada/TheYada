
var x = false;


function disableBrowserAction(tab){
    chrome.browserAction.setIcon({path:"assets/inactive.png"});

    console.log('Disabling ' + tab.url + ' yada!');
    chrome.tabs.executeScript(null, {file: "js/togglecontent.js"});
}

function enableBrowserAction(tab){

    chrome.contentSettings.cookies.set({
      primaryPattern: "http://localhost:8080/*",
      setting: "session_only"
    });

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


chrome.contextMenus.onClicked.addListener(updateState);

// Set up context menu tree at install time.
chrome.runtime.onInstalled.addListener(function() {
  // displays the yada overlay
  chrome.contextMenus.create({
   title: "Show me the Yada",
   contexts:["all"],  // ContextType
   id: "showYada"
  });

  //
  // // Yada Website redirecting
  // chrome.contextMenus.create({
  //  title: "Yada Website",
  //  id: "parent",
  // });
  // // children of Yada Website
  // chrome.contextMenus.create({
  //  title: "New Tab",
  //  parentId: "parent",
  //  id: "newTabChild"
  // });
  // chrome.contextMenus.create({
  //  title: "New Window",
  //  parentId: "parent",
  //  id: "newWindowChild"
  // });
  // chrome.contextMenus.create({
  //  title: "Current Window",
  //  parentId: "parent",
  //  id: "currentWindowChild"
  // });
});
