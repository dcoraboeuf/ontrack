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
            table(containerId, append, data, itemFn);
        };
	}

	function table (containerId, append, items, itemFn) {
	    var containerSelector = '#' + containerId;
	    if (append === true && $(containerSelector).has("tbody").length) {
	        $(containerSelector + " tbody").append(generateTableRows(items, itemFn));
	    } else {
	        // No table defined, or no need to append
	        // Direct filling of the container
	        $(containerSelector).empty();
	        $(containerSelector).append(generateTable(items, itemFn));
	    }
	}

	function fill (contentFn) {
	    return function (containerId, append, items) {
            var html = contentFn(items, append);
            var containerSelector = '#' + containerId;
            $(containerSelector).empty();
            $(containerSelector).append(html);
        }
	}

	function defaultRender (containerId, template, data) {
        table(containerId, data, function (item) {
            return String(item).html();
        });
	}

	function display (id, template, data) {
	    var containerId = id + '-list';
	    if (template.render) {
            template.render(containerId, false, template, data);
	    } else {
	        throw "{0} template has no 'render' function.".format(id);
	    }
	}

	function error (id, message) {
        $('#' + id + '-error').html(message.htmlWithLines());
        $('#' + id + '-error').show();
        loading("#" + id + '-loading', false);
	}

	function load (id) {
	    var selector = '#' + id;
	    // Gets the template
	    var template = $(selector).data('template');
		// Gets the loading information
		var url = template.url;
		if (url) {
		    // Offset and count
		    if (template.more) {
		        url += '&offset=' + template.offset;
		        url += '&count=' + template.count;
		    }
		    // Logging
		    console.log('Template.load id={0},url={1},more={2}'.format(id, url, template.more));
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
					try {
					    display(id, template, data);
					} catch (message) {
				        error(id, message);
					}
				},
				function (message) {
				    error(id, message);
				});
		} else {
		    throw 'No "url" is defined for dynamic section "{0}"'.format(id);
		}
	}

	function init (id, template) {
	    // Logging
	    console.log("Template for {0}:".format(id), template);
        // Associates the template definition with the ID
        $('#' + id).data('template', template);
        // Loading
        load(id);
	}

	function config (input) {
	    return $.extend({}, {
            offset: 0,
            count: 10,
            more: false,
            render: defaultRender
	    }, input);
	}
	
	return {
	    config: config,
	    init: init,

	    asTable: asTable,
	    asLink: asLink
	};
	
} ();