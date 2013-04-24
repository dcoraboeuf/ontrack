var PromotionLevelManagement = function () {

    function up (promotionLevel) {
        order('up', promotionLevel);
    }

    function down (promotionLevel) {
        order('down', promotionLevel);
    }

    function order (direction, promotionLevel) {
        var promotionLevelLoadingIndicator = '#loading-indicator-' + promotionLevel;
        var project = $('#project').val();
        var branch = $('#branch').val();
        // Ajax to perform the re-ordering
        AJAX.get({
            url: 'ui/manage/project/{0}/branch/{1}/promotion_level/{2}/{3}'.format(
                                 project.html(),
                                 branch.html(),
                                 promotionLevel.html(),
                                 direction
                             ),
            loading: {
                mode: 'container',
                el: promotionLevelLoadingIndicator
            },
            successFn: function (data) {
                if (data.success) {
                    location.reload();
                }
            }
        });
    }

    function unlink (validationStampItem) {
        // The validation stamp
        var validationStamp = validationStampItem.attr('data-validationStamp');
        // The previous promotion level
        var promotionLevel = validationStampItem.attr('data-promotionLevel');
        if (promotionLevel && promotionLevel != '') {
            var project = $('#project').val();
            var branch = $('#branch').val();
            // Starts indicating the loading
            var promotionLevelLoadingIndicator = '#loading-indicator-' + promotionLevel;
            // Ajax to perform the unlink
            AJAX.get({
                url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/unlink'.format(
                    project.html(),
                    branch.html(),
                    validationStamp.html()
                ),
                loading: {
                    mode: 'container',
                    el: promotionLevelLoadingIndicator
                },
                successFn: function (data) {
                    if (data.success) {
                        postDnD(validationStampItem, $('#freeValidationStamps'), false);
                    }
                }
            });
        }
    }

    function postDnD (validationStampItem, target, promotionLevel) {
        // Prepares for alignment
        validationStampItem.css('left', '').css('top', '');
        // Appends the validation stamp item to the target
        validationStampItem.appendTo(target);
        // Set-ups the promotion level to this stamp
        if (promotionLevel) {
            validationStampItem.attr('data-promotionLevel', promotionLevel);
        } else {
            validationStampItem.removeAttr('data-promotionLevel');
        }
        // Initializes all promotion drop zones
        initDropZones();
        // Sets the autopromotion button states
        $('.promotionLevelStamps').each(function (index, zone) {
            var promotionLevel = $(zone).attr('data-promotionLevel');
            if ($(zone).find('.validationStamp').length > 0) {
                $('#autoPromote-' +  promotionLevel).show();
            } else {
                $('#autoPromote-' +  promotionLevel).hide();
            }
        });
    }

    function link (validationStampItem, promotionLevelItem) {
        var promotionLevel = promotionLevelItem.attr('data-promotionLevel');
        var validationStamp = validationStampItem.attr('data-validationStamp');
        var project = $('#project').val();
        var branch = $('#branch').val();
        // Starts indicating the loading
        var promotionLevelLoadingIndicator = '#loading-indicator-' + promotionLevel;
        // Ajax to perform the link
        AJAX.get({
            url: 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/link/{3}'.format(
                project.html(),
                branch.html(),
                validationStamp.html(),
                promotionLevel.html()
            ),
            loading: {
                mode: 'container',
                el: promotionLevelLoadingIndicator
            },
            successFn: function (data) {
                if (data.success) {
                    // Clears the DnD style for the validation stamp item
                    postDnD(validationStampItem, promotionLevelItem, promotionLevel);
                }
            }
        });
    }

    function initDropZones () {
        $('.promotionLevelStamps').each(function (index, promotionLevelItem) {
            var dropZone = $('#dropzone-label-' + $(promotionLevelItem).attr('data-promotionLevel'));
            if ($(promotionLevelItem).find('span.validationStamp').length > 0) {
                dropZone.hide();
            } else {
                dropZone.show();
            }
        });
    }

    function init () {
        // All validation stamps are draggable
        $('span.validationStamp').draggable({
            revert: "invalid",
            cursor: "move"
        });
        // Initializes all promotion drop zones
        initDropZones();
        // Droppable zones for the promotion levels
        $('.promotionLevelStamps').droppable({
            activeClass: "ui-state-hover",
            hoverClass: "ui-state-active",
            drop: function( event, ui ) {
                var promotionLevelItem = $(this);
                var validationStampItem = ui.draggable;
                link(validationStampItem, promotionLevelItem);
                return true;
            }
        });
        // Droppable zone for the free validation stamps
        $('#freeValidationStamps').droppable({
            activeClass: "ui-state-hover",
            hoverClass: "ui-state-active",
            drop: function( event, ui ) {
                var validationStampItem = ui.draggable;
                unlink(validationStampItem);
                return true;
            }
        });
    }

    return {

        init: init,
        up: up,
        down: down

    };

} ();

$(document).ready(PromotionLevelManagement.init)