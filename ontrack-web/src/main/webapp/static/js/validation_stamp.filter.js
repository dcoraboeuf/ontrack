var ValidationStampFilter = function () {

    function toggleFilter (chk) {
        var filtered = ($(chk).attr('filtered') == 'true');
        var project = $('#project').val();
        var branch = $('#branch').val();
        var stampId = $(chk).attr('stampId');
        var stamp = $(chk).attr('stamp');
        AJAX.call({
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
                        $(chk).attr('filtered', 'true');
                    } else {
                        $('#stamp_line_{0}'.format(stampId)).removeClass('filtered');
                        $(chk).attr('filtered', 'false');
                    }
                }
            }
        });
    }

    return {
        toggleFilter: toggleFilter
    };

} ();