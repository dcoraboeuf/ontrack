var PromotionLevelManagement = function () {

    function link (validationStampItem, promotionLevelItem) {
        var promotionLevel = promotionLevelItem.attr('data-promotionLevel');
        var validationStamp = validationStampItem.attr('data-validationStamp');
        var project = $('#project').val();
        var branch = $('#branch').val();
        // Starts indicating the loading
        var promotionLevelLoadingIndicator = '#loading-indicator-' + promotionLevel;
        Application.loading(promotionLevelLoadingIndicator, true);
        // TODO Ajax to perform the link
        Application.ajaxGet(
            'ui/manage/promotion_level/{0}/{1}/{2}/link/{3}'.format(
                project.html(),
                branch.html(),
                validationStamp.html(),
                promotionLevel.html()
            ),
            function (data) {
                Application.loading(promotionLevelLoadingIndicator, false);
                if (data.success) {
                    // Clears the DnD style for the validation stamp item
                    validationStampItem.removeAttr('style');
                    // Appends the validation stamp item to the promotion level
                    validationStampItem.appendTo(promotionLevelItem);
                    // Attaches the promotion level to this stamp
                    validationStampItem.attr('data-promotionLevel', promotionLevel);
                    // Initializes all promotion drop zones
                    initDropZones();
                }
            },
            function (message) {
                Application.loading(promotionLevelLoadingIndicator, false);
                Application.displayError(message);
            }
        );
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
                return false;
            }
        });
        // Droppable zone for the free validation stamps
        $('#freeValidationStamps').droppable({
            activeClass: "ui-state-hover",
            hoverClass: "ui-state-active",
            drop: function( event, ui ) {
                alert(ui.draggable.attr('data-validationStamp'));
                return false;
            }
        });
    }

    return {

        init: init

    };

} ();

$(document).ready(PromotionLevelManagement.init)