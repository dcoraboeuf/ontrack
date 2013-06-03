define(['ajax'], function (ajax) {

    var project = $('#project').val();
    var branch = $('#branch').val();

    function toggleFilter (box) {
        var stampId = Number($(box).attr('stampId'));
        var stamp = $(box).attr('stamp');
        var filtered = ($(box).attr('filtered') == 'true');
        ajax.call({
            url: 'ui/admin/project/{0}/branch/{1}/validation_stamp/{2}/filter'.format(
                project,
                branch,
                stamp
            ),
            method: (filtered ? 'DELETE' : 'PUT'),
            loading: {
                el: $('stamp_{0}'.format(stamp))
            },
            successFn: function (ack) {
                if (ack.success) {
                    filtered = !filtered;
                    if (filtered) {
                        $('#stamp_line_{0}'.format(stampId)).addClass('filtered');
                        $(box).attr('filtered', 'true');
                    } else {
                        $('#stamp_line_{0}'.format(stampId)).removeClass('filtered');
                        $(box).attr('filtered', 'false');
                    }
                }
            }
        });
    }

    // For each checkbox
    $('.validation-stamp-check').each(function (index, box) {
        // Initial state
        var filtered = ($(box).attr('filtered') == 'true');
        if (filtered) {
            $(box).attr('checked', 'checked');
        }
        // Action
        $(box).click(function () {
            toggleFilter($(box));
        });
    });

});