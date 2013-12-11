angular.module('ontrack.admin.accounts', [])
    .config(function config($stateProvider) {
        $stateProvider.state('accounts', {
            url: '/admin/account',
            views: {
                "main": {
                    controller: 'AccountsCtrl',
                    templateUrl: 'app/accounts/accounts.tpl.html'
                }
            }
        })
    })
    .controller('AccountsCtrl', function ($scope, $state, $translate, pageService) {
        // Page definition
        $scope.page = {
            title: $translate('accounts'),
            close: function () {
                $state.go('home')
            }
        };
        // Breadcrumbs
        pageService.setBreadcrumbs([
            {
                text: $translate('home'),
                link: '/home'
            }, {
                text: $translate('accounts')
            }
        ]);
    })
;