angular.module('ontrack.signin', [
        'ui.router'
    ])

    .config(function config($stateProvider) {
        $stateProvider.state('signin', {
            url: '/signin',
            views: {
                "main": {
                    controller: 'SigninCtrl',
                    templateUrl: 'app/signin/signin.tpl.html'
                }
            },
            data: { pageTitle: 'Sign in' }
        })
    })

    .controller('SigninCtrl', function SigninController($scope) {
        $scope.name = '';
        $scope.password = '';
        $scope.error = '';
        $scope.signin = function () {

        }
    })

;

