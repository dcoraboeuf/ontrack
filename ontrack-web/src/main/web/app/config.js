angular.module('ontrack.config', [])
    .service('config', function () {
        // var _version = '2.0-SNAPSHOT';
        var _server = '.';
        return {
            api: function (path) {
                return _server + '/api/' + path
            }
        }
    })
;