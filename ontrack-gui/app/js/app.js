'use strict';


// Declare app level module which depends on filters, and services
angular.module('ontrack', [
        'ngRoute',
        'ui.bootstrap',
        'ontrack.config',
        'ontrack.filters',
        'ontrack.services',
        'ontrack.directives',
        'ontrack.controllers'
    ]).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/home', {templateUrl: 'partials/home.html'});
        $routeProvider.when('/project/:projectName', {templateUrl: 'partials/project.html'});
        $routeProvider.otherwise({redirectTo: '/home'});
    }]);
