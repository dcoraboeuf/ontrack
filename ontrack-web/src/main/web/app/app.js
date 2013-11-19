'use strict';

// Declare app level module which depends on filters, and services
angular.module('ontrack', [
        'ngRoute',
        'ui.bootstrap'
    ])
    .config(function ($routeProvider, $httpProvider) {
        console.log('App init');
        // Authentication using cookies and CORS protection
        $httpProvider.defaults.withCredentials = true;
        // Route set-up
        $routeProvider.otherwise({redirectTo: '/home'});
    })
    // .run(['AuthenticationService', function (authenticationService) {
    //     authenticationService.init()
    // }])
    .controller('AppCtrl', function AppCtrl($scope) {
    })
;
