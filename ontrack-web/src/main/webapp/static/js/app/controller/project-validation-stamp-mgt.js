define(['render', 'jquery', 'ajax'], function (render, $, ajax) {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch'.format(config.project)
        },
        render: render.asSimpleTemplate(
            'project-validation-stamp-mgt',
            function (branches, config) {
                return {
                    branchId: config.branchId,
                    branchTitle: 'validation_stamp.mgt.{0}'.format(config.branchId).loc(),
                    branches: branches
                }
            },
            function (config) {
                var container = $('#' + config.branchId);
                // On selection of the branch
                container.find('select.project-validation-stamp-mgt-branch').change(function (e) {
                    var branch = $(this).val();
                    // Gets the list of validation stamps
                    ajax.get({
                        url: 'ui/manage/project/{0}/branch/{1}/validation_stamp'.format(config.project, branch),
                        loading: {
                            mode: 'container',
                            el: container.find('div.loading')
                        },
                        errorFn: ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn(container.find('.project-validation-stamp-mgt-branch-error'))),
                        successFn: function (validationStamps) {

                        }
                    })
                })
            }
        )
    }

});