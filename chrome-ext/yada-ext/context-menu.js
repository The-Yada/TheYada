


// https://developer.chrome.com/extensions/contextMenus
// see onClicked events for information on info and tab
// (tabs is set in Manifest.json permissions)
// this will give us access to tabs.Tab.url

let showMeTheYada = function(info, tab) {
  //TODO: determine which menu item was clicked
  //TODO: tab.url
  //TODO: redirect to website or show yada

  // find yadas array matching the url
  // displays yada overlaying DOM
}


chrome.contextMenus.onClicked.addListener(showMeTheYada);

// Set up context menu tree at install time.
chrome.runtime.onInstalled.addListener(function() {
  // displays the yada overlay
  chrome.contextMenus.create({
   title: "Show me the Yada",
   contexts:["all"],  // ContextType
   id: "showYada"
  });

  // Yada Website redirecting
  chrome.contextMenus.create({
   title: "Yada Website",
   id: "parent",
  });
  // children of Yada Website
  chrome.contextMenus.create({
   title: "New Tab",
   parentId: "parent",
   id: "newTabChild"
  });
  chrome.contextMenus.create({
   title: "New Window",
   parentId: "parent",
   id: "newWindowChild"
  });
  chrome.contextMenus.create({
   title: "Current Window",
   parentId: "parent",
   id: "currentWindowChild"
  });
});
