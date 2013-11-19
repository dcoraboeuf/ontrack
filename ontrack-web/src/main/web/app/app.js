'use strict';

// Declare app level module which depends on filters, and services
angular.module('ontrack', [
        'ui.bootstrap',
        'ui.router',
        'ontrack.home',
        'ontrack.config'
    ])
    .config(function ($httpProvider, $urlRouterProvider) {
        console.log('App init');
        // Authentication using cookies and CORS protection
        $httpProvider.defaults.withCredentials = true;
        // Route set-up
        $urlRouterProvider.otherwise('/home');
    })
    // .run(['AuthenticationService', function (authenticationService) {
    //     authenticationService.init()
    // }])
    .controller('AppCtrl', function AppCtrl($scope) {
    })
;
