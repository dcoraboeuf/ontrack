angular.module('ontrack.home', [
    ])

    .config(function config($route) {
        console.log('Home init');
        $route.when('/home', {templateUrl: 'app/home/home.html', controller: 'HomeCtrl'});
    })

    .controller('HomeCtrl', function HomeController($scope) {
        console.log('Registering /home');
    })

;

