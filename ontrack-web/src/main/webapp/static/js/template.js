var Template = function () {
	
	function generateTableRows (items, rowFn) {
		var html = '';
		$.each (items, function (index, item) {
			html += '<tr><td>';
				html += rowFn(item);
			html += '</td></tr>';
		});
		return html;
	}

	function generateTable (items, rowFn) {
		var html = '<table class="table table-hover"><tbody>';
		html += generateTableRows(items, rowFn);
		html += '</tbody></table>';
		return html;
	}
	
	function asLink (url) {
	    return function (item) {
			return '<a href="{0}/{2}" title="{3}">{2}</a>'.format(url, item.id, item.name.html(), item.description.html());
	    };
	}

	function asTable (itemFn) {
        return function (containerId, append, config, data) {
            table(containerId, append, config, data, itemFn);
        };
	}

	function table (containerId, append, config, items, itemFn) {
	    var containerSelector = '#' + containerId;
	    if (append === true && $(containerSelector).has("tbody").length) {
	        $(containerSelector + " tbody").append(generateTableRows(items, itemFn));
	    } else {
	        // No table defined, or no need to append
	        // Some items
	        if (items.length && items.length > 0) {
                // Direct filling of the container
                $(containerSelector).empty();
                $(containerSelector).append(generateTable(items, itemFn));
	        }
	        // No items
	        else {
	            $(containerSelector).empty();
	            $(containerSelector).append('<div class="alert">{0}</div>'.format(config.placeholder));
            }
	    }
	}

	function fill (contentFn) {
	    return function (containerId, append, config, items) {
            var html = contentFn(items, append);
            var containerSelector = '#' + containerId;
            $(containerSelector).empty();
            $(containerSelector).append(html);
        }
	}

	function defaultRender (containerId, append, config, data) {
        table(containerId, false, config, data, function (item) {
            return String(item).html();
        });
	}

	function moreStatus(id, config, data) {
	    if (config.more) {
	        // console.log("Template.moreStatus, id={0}, data={1}, count={2}".format(id, data.length, config.count));
	        var dataCount = config.dataLength(data);
	        config.offset += dataCount;
	        var hasMore = (dataCount >= config.count);
	        if (hasMore) {
	            $('#'+ id + '-more-section').show();
	        } else {
	            $('#'+ id + '-more-section').hide();
	        }
	    }
	}

	function display (id, append, config, data) {
	    var containerId = id + '-list';
	    if (config.render) {
            config.render(containerId, append, config, data);
            if (config.postRenderFn) {
                config.postRenderFn();
            }
	    } else {
	        throw "{0} template has no 'render' function.".format(id);
	    }
	}

	function error (id, message) {
        $('#' + id + '-error').html(String(message).htmlWithLines());
        $('#' + id + '-error').show();
        Application.loading("#" + id + '-loading', false);
	}

	function more (id) {
        load(id, true);
	}

	function reload (id) {
	    var selector = '#' + id;
	    // Gets the template
	    var config = $(selector).data('template-config');
	    // Reinitializes the offset and count
	    config.count = 10;
	    config.offset = 0;
	    // Reloads
	    load(id, false);
	}

	function load (id, append) {
	    var selector = '#' + id;
	    // Gets the template
	    var config = $(selector).data('template-config');
		// Gets the loading information
		var url = config.url;
		if (url) {
		    // Offset and count
		    if (config.more) {
		        url += '&offset=' + config.offset;
		        url += '&count=' + config.count;
		    }
		    // Logging
		    // console.log('Template.load id={0},url={1},more={2},append={3}'.format(id, url, config.more, append));
			// Starts loading
			Application.loading("#" + id + '-loading', true);
	  		$('#' + id + '-error').hide();
            // Call
            Application.ajaxGet (
                url,
                function (data) {
                    // Loading done
                    Application.loading("#" + id + '-loading', false);
                    // Uses the data
                    // try {
                        display(id, append, config, data);
                        // Management of the 'more'
                        moreStatus(id, config, data);
                    // } catch (message) {
                    //     error(id, message);
                    // }
                },
                function (message) {
                    error(id, message);
                }
            );
		} else {
		    throw 'No "url" is defined for dynamic section "{0}"'.format(id);
		}
	}

	function init (id, config) {
	    // Logging
	    // console.log("Template for {0}:".format(id), config);
        // Associates the template definition with the ID
        $('#' + id).data('template-config', config);
        // Loading
        load(id, false);
        // Reloading?
        if (config.refresh) {
            setInterval(function () {
                reload(id);
            }, config.refreshInterval);
        }
	}

	function config (input) {
	    return $.extend({}, {
	        refresh: false,
	        refreshInterval: 30000, // 30 seconds
            offset: 0,
            count: 10,
            more: false,
            render: defaultRender,
            dataLength: function (data) {
                return data.length;
            },
            placeholder: loc('general.empty')
	    }, input);
	}
	
	return {
	    config: config,
	    init: init,
	    more: more,
	    reload: reload,

	    asTable: asTable,
	    asLink: asLink,
	    fill: fill
	};
	
} ();