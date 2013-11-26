angular.module('ontrack.service.dialog', ['ui.bootstrap'])
    .service('dialog', function ($q, $modal) {
        var self = {};
        self.confirm = function (config) {
            // Parameters
            var params;
            if (!angular.isObject(config)) {
                params = {
                    text: config
                };
            } else {
                params = angular.extend({
                    // TODO Default title
                    // TODO Default button titles
                }, config);
            }
            // Creates a task
            // var deferred = $q.defer();
            // Displays the modal dialog
            var dialog = $modal.open({
                templateUrl: 'app/services/dialog.tpl.html',
                controller: function ($scope, $modalInstance) {
                    console.log('In controller');
                    $scope.ok = function () {
                        $modalInstance.close('ok');
                    };
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                }
            });
            // TODO Waits for it to close
            dialog.result.then(function () {
                // deferred.resolve();
            });
            // Returns the task
            // return deferred.promise;
        };
        return self;
    })
;