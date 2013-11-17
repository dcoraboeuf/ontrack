'use strict';

/* Configuration */

var _version = '2.0-SNAPSHOT';
// @prod
// var _server = 'http://localhost:8080/ontrack/';
// @dev
var _server = '../';

angular.module('ontrack.config', [])
    .value('config', {
        version: _version,
        api: function (path) {
            return _server + 'api/' + path
        }
    });
