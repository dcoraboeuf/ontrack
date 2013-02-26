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
                    // Clears the drop zone label
                    $('#dropzone-label-' + promotionLevel).hide();
                }
            },
            function (message) {
                Application.loading(promotionLevelLoadingIndicator, false);
                Application.displayError(message);
            }
        );
    }

    function init () {
        // All validation stamps are draggable
        $('span.validationStamp').draggable({
            revert: "invalid",
            cursor: "move"
        });
        // Initializes all promotion drop zones
        $('.promotionLevelStamps').each(function (index, promotionLevelItem) {
            if ($(promotionLevelItem).has('span.validationStamp')) {
                $('#dropzone-label-' + $(promotionLevelItem).attr('data-promotionLevel')).hide();
            }
        });
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