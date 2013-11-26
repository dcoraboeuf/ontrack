angular.module('ontrack.directives', [])
/**
 * <ot-todo />
 */
    .directive('otTodo', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directives/otTodo.tpl.html',
            transclude: true
        }
    })
/**
 * <ot-promotion-level-link self=<> [name='hide'] />
 * @param self PromotionLevelResource
 * @param name If set to 'hide', does not show the promotion level name, if not set, show it (default)
 */
    .directive('otPromotionLevelLink', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                promotionLevel: '=self',
                name: '@name'
            },
            templateUrl: 'app/directives/otPromotionLevelLink.tpl.html'
        }
    })
/**
 * <ot-page> that creates a standard page layout.
 */
    .directive('otPage', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directives/otPage.tpl.html',
            transclude: true
        }
    })
;