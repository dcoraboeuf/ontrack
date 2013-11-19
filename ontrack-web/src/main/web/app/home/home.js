angular.module('ontrack.home', [
    ])

    .config(function config($routeProvider) {
        $routeProvider.when('/home', {templateUrl: 'home/home.html', controller: 'HomeCtrl'});
    })

    .controller('HomeCtrl', function HomeController($scope) {
    })

;

