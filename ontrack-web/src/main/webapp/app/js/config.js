'use strict';

/* Configuration */

var _version = '2.0-SNAPSHOT'

angular.module('ontrack.config', [])
    .value('config', {
        version: '2.0-SNAPSHOT',
        server: 'http://localhost:8080/ontrack/'
    });
