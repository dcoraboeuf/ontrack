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
        });
        $stateProvider.state('account-create', {
            url: '/admin/account/create',
            views: {
                main: {
                    controller: 'AccountCreateCtrl',
                    templateUrl: 'app/accounts/account-create.tpl.html'
                }
            }
        });
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
            $scope.accounts = accounts;
            $scope.page.commands = [
                {
                    id: 'account-create',
                    name: 'account.new',
                    icon: 'plus',
                    link: accounts.links['accountCreate'],
                    action: function () {
                        $state.go('account-create')
                    }
                }
            ];
        });
    })
    .controller('AccountCreateCtrl', function ($scope, $state, $translate, pageService) {
        // Page definition
        $scope.page = {
            title: $translate('account.new'),
            close: function () {
                $state.go('accounts')
            }
        };
        // Breadcrumbs
        pageService.setBreadcrumbs([
            {
                text: $translate('home'),
                link: '/home'
            },
            {
                text: $translate('accounts'),
                link: '/admin/account'
            },
            {
                text: $translate('account.new')
            }
        ]);
    })
;