var sim = {
  getSimInfo: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'Sim', 'getSimInfo', []);
  },
  hasReadPermission: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'Sim', 'hasReadPermission', []);
  },
  requestReadPermission: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'Sim', 'requestReadPermission', []);
  }
};

cordova.addConstructor(function() {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.sim = sim;
  return window.plugins.sim;
});
