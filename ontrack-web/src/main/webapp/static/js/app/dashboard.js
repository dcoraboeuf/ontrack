define(['ajax','jquery','render'], function (ajax, $, render) {

    // Context
    var context = location.href.substring(location.href.indexOf('dashboard'));
    // Initial index
    var index = 0;

    function getPageLayout(page) {
        if (page.layoutId) {
            return page.layoutId;
        } else {
            return 'dashboard-layout-default';
        }
    }

    function getPageSectionLayout(section) {
        if (section.layoutKey) {
            return section.layoutKey;
        } else {
            return 'default';
        }
    }

    function addSectionToLayout(layoutData, layoutKey, sectionRenderFn) {
        if (!layoutData[layoutKey]) {
            layoutData[layoutKey] = [];
        }
        layoutData[layoutKey].push(sectionRenderFn);
    }

    function displayPage(page) {
        // Title
        $('#page-title').text(page.title);

        // Content

        // Page layout to use
        var layoutId = getPageLayout(page);
        render.withTemplate(layoutId, function (layout) {
            // Data for the layout
            var layoutData = {};
            // Loops over all sections of the page
            $.each(page.sections, function (i, section) {
                // Layout key for the section
                var layoutKey = getPageSectionLayout(section);
                // TODO Rendering function
                var sectionRenderFn = function () {};
                // Adds to the layout
                addSectionToLayout(layoutData, layoutKey, sectionRenderFn);
            });
            // Renders the layout
            $('#page-content').html(layout(layoutData));
        });
    }

    // Refresh function
    function refresh () {
        // Gets the URL
        var url = 'ui/{0}/page/{1}'.format(context, index++);
        // Call
        ajax.get({
            url: url,
            loading: {
                el: $('#dashboard-loading'),
                mode: 'toggle'
            },
            successFn: displayPage,
            errorFn: ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn($('#dashboard-error')))
        })
    }

    // Refreshes the dashboard content
    refresh();
    setInterval(refresh, 10000);

})