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
	
	function tableRowLink (url) {
	    return function (item) {
			return '<a href="{0}/{2}" title="{3}">{2}</a>'.format(url, item.id, item.name.html(), item.description.html());
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
	
	return {
	    fill: fill,
		table: table,
		tableRowLink: tableRowLink
	};
	
} ();