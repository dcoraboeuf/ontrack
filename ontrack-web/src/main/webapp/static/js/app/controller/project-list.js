define(['render', 'ajax'], function (render, ajax) {

    return {
        url: 'ui/manage/project',
        render: render.asTableTemplate('project-row', function (config) {
            $('.project-branches').each(function (index, td) {
                var project = $(td).attr('data-project');
                ajax.get({
                    url: 'ui/manage/project/{0}/branch/status'.format(project),
                    loading: {
                        el: $(td)
                    },
                    errorFn: ajax.simpleAjaxErrorFn(function (message) {
                        $(td).append(
                            $('<div></div>')
                                .addClass('alert')
                                .addClass('alert-error')
                                .text(message)
                        )
                    }),
                    successFn: function (statuses) {
                        // Pre-processing
                        $.each(statuses, function (index, status) {
                            var promotion = null;
                            for(var i = status.promotions.length - 1; i >= 0; i--) {
                                var currentPromotion = status.promotions[i];
                                if (currentPromotion.buildSummary && currentPromotion.buildSummary != null) {
                                    promotion = currentPromotion;
                                    break;
                                }
                            }
                            status.lastPromotion = promotion;
                        });
                        // Rendering
                        render.renderInto(
                            $(td),
                            'project-branches',
                            statuses
                        )
                    }
                })
            })
        })
    }

});