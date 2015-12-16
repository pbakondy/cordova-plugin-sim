exports.defineAutoTests = function() {
  describe('SIM Information (window.plugins.sim)', function () {
    it('should exist', function() {
      expect(window.plugins.sim).toBeDefined();
    });

    it('should contain getSimInfo that is a function', function() {
      expect(window.plugins.sim.getSimInfo).toBeDefined();
      expect(typeof window.plugins.sim.getSimInfo).toBe('function');
    });

  });
};

exports.defineManualTests = function(contentEl, createActionButton) {
  var logMessage = function (message, color) {
    var log = document.getElementById('info');
    var logLine = document.createElement('div');
    if (color) {
      logLine.style.color = color;
    }
    logLine.innerHTML = message;
    log.appendChild(logLine);
  };

  var clearLog = function () {
    var log = document.getElementById('info');
    log.innerHTML = '';
  };

  var device_tests = '<h3>Press Dump SIM button to get SIM information</h3>' +
    '<div id="dump_sim"></div>' +
    'Expected result: Status box will get updated with SIM info. (i.e. carrierName, countryCode, etc)';

  contentEl.innerHTML = '<div id="info"></div>' + device_tests;

  createActionButton('Dump SIM', function() {
    clearLog();
    window.plugins.sim.getSimInfo(
      function(result) {
        logMessage(JSON.stringify(result));
      },
      function(error) {
        logMessage(JSON.stringify(error));
      }
    );
  }, 'dump_sim');
};
