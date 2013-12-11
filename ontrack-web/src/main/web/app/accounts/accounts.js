angular.module('ontrack.admin.accounts', [
        'ontrack.service.account'
    ])
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
    .controller('AccountsCtrl', function ($scope, $state, $translate, pageService, accountService) {
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
            },
            {
                text: $translate('accounts')
            }
        ]);
        // Loading the list of accounts
        accountService.getAccountList().success(function (accounts) {
            $scope.accounts = accounts
        });
    })
;