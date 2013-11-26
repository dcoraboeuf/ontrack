angular.module('ontrack.service.dialog', ['ui.bootstrap'])
    .service('dialog', function ($q) {
        // FIXME Waiting for angular-ui/bootstrap to fix problems with $modal
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
            var deferred = $q.defer();
            // FIXME Replaces the confirm() call by the use of $modal
            if (confirm(params.text)) {
                deferred.resolve({});
            } else {
                deferred.reject({});
            }
            // OK
            return deferred.promise;
        };
        return self;
    })
;