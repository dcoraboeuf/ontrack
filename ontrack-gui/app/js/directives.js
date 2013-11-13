'use strict';

/* Directives */


var otDirectives = angular.module('ontrack.directives', []);
otDirectives.directive('appVersion', ['version', function (version) {
    return function (scope, elm, attrs) {
        elm.text(version);
    };
}]);
otDirectives.directive('otPageClose', function () {
    return {
        restrict: 'E',
        templateUrl: 'directives/page-close.html',
        scope: {
            href: '@href'
        }
    };
});