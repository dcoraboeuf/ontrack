'use strict';

/* Directives */


angular.module('ontrack.directives', [])
    .directive('appVersion', ['version', function (version) {
        return function (scope, elm, attrs) {
            elm.text(version);
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
    .directive('otNavUser', function () {
        return {
            restrict: 'E',
            templateUrl: 'directives/nav-user.html'
        }
    })
;