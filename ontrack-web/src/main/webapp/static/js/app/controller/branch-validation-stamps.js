define(['render','ajax','dynamic','app/component/validationStamp'], function (render, ajax, dynamic, validationStamp) {

    function upValidationStamp (project, branch, stamp) {
        ajax.put({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/up'.format(project, branch, stamp),
            loading: {
                el: $('#validation-stamp-{0}-order-loading'.format(stamp)),
                mode: 'container'
            },
            successFn: function () {
                dynamic.reloadSection('branch-validation-stamps');
            }
        })
    }

    function downValidationStamp (project, branch, stamp) {
        ajax.put({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/down'.format(project, branch, stamp),
            loading: {
                el: $('#validation-stamp-{0}-order-loading'.format(stamp)),
                mode: 'container'
            },
            successFn: function () {
                dynamic.reloadSection('branch-validation-stamps');
            }
        })
    }


    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/validation_stamp'.format(config.project, config.branch);
        },
        preProcessingFn: function (config, stamps, append) {
            var count = stamps.length;
            $.each(stamps, function (index, stamp) {
                stamp.admin = (config.admin == 'true');
                if (index == 0) {
                    stamp.first = true;
                }
                if (index == count - 1) {
                    stamp.last = true;
                }
            });
            return stamps;
        },
        render: render.asTableTemplate('branch-validation-stamp', function (config) {
            // Ordering of the validation stamps
            $('.validation-stamp-order').each(function (index, link) {
                var stamp = $(link).attr('order-stamp');
                var direction = $(link).attr('order-direction');
                $(link).unbind('click');
                $(link).click(function () {
                    if (direction == 'up') {
                        upValidationStamp(config.project, config.branch, stamp);
                    } else {
                        downValidationStamp(config.project, config.branch, stamp);
                    }
                });
            });
            // Changing the owner
            $('.validation-stamp-owner').each(function (index, link) {
                var stamp = $(link).attr('owner-stamp');
                $(link).unbind('click');
                $(link).click(function () {
                    validationStamp.changeOwner(config.project, config.branch, stamp, function () {
                        dynamic.reloadSection('branch-validation-stamps');
                    });
                });
            });
        })
    }

});