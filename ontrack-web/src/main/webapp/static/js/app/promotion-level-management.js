define(['jquery','ajax','jquery-ui'], function ($, ajax) {

    var project = $('#project').val();
    var branch = $('#branch').val();

    // Promotion level ordering
    function order (direction, promotionLevel) {
        var promotionLevelLoadingIndicator = '#loading-indicator-' + promotionLevel;
        var project = $('#project').val();
        var branch = $('#branch').val();
        // Ajax to perform the re-ordering
        ajax.get({
            url: 'ui/manage/project/{0}/branch/{1}/promotion_level/{2}/{3}'.format(
                project,
                branch,
                promotionLevel,
                direction
            ),
            loading: {
                mode: 'container',
                el: $(promotionLevelLoadingIndicator)
            },
            successFn: function (data) {
                if (data.success) {
                    location.reload();
                }
            }
        });
    }

    // Promotion level up
    function promotionUp(promotionLevel) {
        order('up', promotionLevel);
    }

    // Promotion level down
    function promotionDown(promotionLevel) {
        order('down', promotionLevel);
    }

    // Initializes all promotion drop zones
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

    function link (validationStampItem, promotionLevelItem) {
        var promotionLevel = promotionLevelItem.attr('data-promotionLevel');
        var validationStamp = validationStampItem.attr('data-validationStamp');
        // Starts indicating the loading
        var promotionLevelLoadingIndicator = '#loading-indicator-' + promotionLevel;
        // Ajax to perform the link
        ajax.get({
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
        initAutoPromote();
    }

    function unlink (validationStampItem) {
        // The validation stamp
        var validationStamp = validationStampItem.attr('data-validationStamp');
        // The previous promotion level
        var promotionLevel = validationStampItem.attr('data-promotionLevel');
        if (promotionLevel && promotionLevel != '') {
            // Starts indicating the loading
            var promotionLevelLoadingIndicator = '#loading-indicator-' + promotionLevel;
            // Ajax to perform the unlink
            ajax.get({
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

    function autoPromote (button) {
        // Gets the promotion level
        var promotionLevel = $(button).attr('data-promotionLevel');
        // Current state of the autopromotion
        var currentAutoPromote = $(button).attr('data-autoPromote');
        // URL
        var url = 'ui/manage/project/{0}/branch/{1}/promotion_level/{2}/autopromote'.format(
            project.html(),
            branch.html(),
            promotionLevel.html()
        );
        if (currentAutoPromote == 'true') {
            url += '/unset';
        } else {
            url += '/set';
        }
        // Call
        ajax.put({
            url: url,
            loading: {
                el: button
            },
            successFn: function (flag) {
                if (flag.set) {
                    $(button).attr('data-autoPromote', 'true');
                    $(button).text('promotion_level.management.notauto'.loc());
                } else {
                    $(button).attr('data-autoPromote', 'false');
                    $(button).text('promotion_level.management.auto'.loc());
                }
                initAutoPromote();
            }
        });
    }

    // Auto-promotion
    function initAutoPromote() {
        $('.promotionLevelStamps').each(function (index, zone) {
            var promotionLevel = $(zone).attr('data-promotionLevel');
            if ($(zone).find('.validationStamp').length > 0) {
                $('#autoPromote-' + promotionLevel).show();
            } else {
                $('#autoPromote-' + promotionLevel).hide();
            }
            if ($('#autoPromote-' + promotionLevel).attr('data-autoPromote') == 'true') {
                $('#autoPromoteFlag-' + promotionLevel).show();
            } else {
                $('#autoPromoteFlag-' + promotionLevel).hide();
            }
        });
        $('.autoPromote').each(function (index, button) {
            $(button).unbind('click');
            $(button).click(function () {
                autoPromote($(button));
            });
        });
    }

    // Actions

    $('.icon-arrow-up').each(function (index, action) {
        var promotionLevel = $(action).attr('data-promotionLevel');
        $(action).click(function () {
            promotionUp(promotionLevel);
        });
    });

    $('.icon-arrow-down').each(function (index, action) {
        var promotionLevel = $(action).attr('data-promotionLevel');
        $(action).click(function () {
            promotionDown(promotionLevel);
        });
    });

    initAutoPromote();

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

});