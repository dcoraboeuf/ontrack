angular.module('ontrack.home', [
        'ui.router'
    ])

    .config(function config($stateProvider) {
        console.log('Home init');
        $stateProvider.state('home', {
            url: '/home',
            views: {
                "main": {
                    controller: 'HomeCtrl',
                    templateUrl: 'app/home/home.tpl.html'
                }
            },
            data: { pageTitle: 'Home' }
        })
    })

    .controller('HomeCtrl', function HomeController($scope) {
        console.log('Registering /home');
    })

;

