/*******************************
* BACKGROUND & CONTEXT MENU
* (chrome extension background file)
********************************/
(function() {

  var x = false;
  var currentId = null;

  /*******************************
  * hides iframe, reverts icon
  ********************************/
  function disableBrowserAction(tab){
      chrome.browserAction.setIcon({path:"assets/yada-bulb-off-browser-icon.png"});

      console.log('Disabling ' + tab.url + ' yada!');
      chrome.tabs.executeScript(null, {file: "js/togglecontent.js"});
  }

  /*******************************
  * triggers yada iframe insertion, cookies, icon change, and animation
  ********************************/
  function enableBrowserAction(tab){


      let msg = {greeting: "let's reload the ext animation"};

      chrome.contentSettings.cookies.set({
        primaryPattern: "http://localhost:8080/*",
        setting: "session_only"
      });

      chrome.browserAction.setIcon({path:"assets/yada-bulb-on-browser-icon.png"});

      console.log('Enabling ' + tab.url + ' yada!');
      chrome.tabs.executeScript(null, {file: "js/content.js"});

      if (currentId === null) {
        return
      } else {
        chrome.tabs.sendMessage(currentId, msg);
      }

  }


  /*******************************
  * toggles state and proper function
  ********************************/
  function updateState(tab, pageUrl){

      if(x==false){
          x=true;
          enableBrowserAction(tab);
      }else{
          x=false;
          disableBrowserAction(tab);
      }
  }


  /*******************************
  * callback for event listener
  * listening to ext.js run()
  * sets current tab id
  ********************************/
  function setTab(message, sender) {
    currentId = sender.tab.id
    return sender
  }

  /*******************************
  * CONTEXT MENU
  ********************************/
  chrome.runtime.onInstalled.addListener(function() {
    // displays the yada overlay
    chrome.contextMenus.create({
     title: "Show me the Yada",
     contexts:["all"],  // ContextType
     id: "showYada"
    });
  });

  /*******************************
  * EVENT LISTENERS
  ********************************/
  // clicking chrome extension icon
  chrome.browserAction.onClicked.addListener(updateState);
  // disable on tab action
  chrome.tabs.onActivated.addListener(disableBrowserAction);
  // toggles view with context menu
  chrome.contextMenus.onClicked.addListener(updateState);
  // sets tab id
  chrome.runtime.onMessage.addListener(setTab);

})()
