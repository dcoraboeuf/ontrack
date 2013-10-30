define(['jquery', 'ajax', 'jquery-ui'], function ($, ajax) {

    function userLookup(field) {
        return function (query, processFn) {
            ajax.get({
                url: 'ui/admin/account/lookup/{0}'.format(query),
                successFn: function (accountSummaries) {
                    var accountLabels = [];
                    var accountsByNames = {};
                    $.data(field, 'accountLabels', accountLabels);
                    $.data(field, 'accountsByNames', accountsByNames);
                    $.each(accountSummaries, function (i, accountSummary) {
                        var accountLabel = accountSummary.name + ' - ' + accountSummary.fullName;
                        accountsByNames[ accountLabel] = accountSummary;
                        accountLabels.push(accountLabel);
                    });
                    processFn(accountLabels);
                }
            })
        }
    }

    function init(field) {
        field.typeahead({
            source: userLookup(field),
            matcher: function () {
                return true;
            },
            sorter: function (items) {
                return items;
            },
            highlighter: function (item) {
                var regex = new RegExp('(' + this.query + ')', 'gi');
                return item.replace(regex, "<strong>$1</strong>");
            },
            updater: function (accountLabel) {
                var accountsByNames = $.data(field, 'accountsByNames');
                $.data(field, 'selectedAccount', accountsByNames[accountLabel]);
                return accountLabel;
            }
        });
    }

    return {
        init: init
    }

});