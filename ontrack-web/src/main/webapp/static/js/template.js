var Template = function () {
	
	function list (items, cellFn) {
		var html = '<table class="table table-hover"><tbody>';
		$.each (items, function (index, item) {
			html += '<tr><td>';
				html += cellFn(item);
			html += '</td></tr>';
		});
		html += '</tbody></table>';
		return html;
	}
	
	function links (items, url) {
		return list (items, function (item) {
			return '<a href="{0}/{2}" title="{3}">{2}</a>'.format(url, item.id, item.name.html(), item.description.html());
		});
	}
	
	return {
		links: links,
		list: list
	};
	
} ();