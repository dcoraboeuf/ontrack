'use strict';

/* Directives */


angular.module('ontrack.directives', [])
    .directive('appVersion', ['config', function (config) {
        return function (scope, elm) {
            elm.text('v' + config.version);
        };
    }])
    .directive('otPageClose', function () {
        return {
            restrict: 'E',
            templateUrl: 'directives/page-close.html',
            scope: {
                href: '@href'
            }
        };
    })
;