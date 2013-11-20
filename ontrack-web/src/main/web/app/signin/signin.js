angular.module('ontrack.signin', [
        'ui.router',
        'ontrack.service.security'
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

    .controller('SigninCtrl', function SigninController($scope, $location, securityService) {
        $scope.name = '';
        $scope.password = '';
        $scope.error = '';
        $scope.signin = function () {
            securityService.authenticate($scope.name, $scope.password, function () {
                // TODO Redirect to the page in the scope
                $location.path('/home');
            }, function (error) {
                $scope.error = error
            })
        }
    })

;

