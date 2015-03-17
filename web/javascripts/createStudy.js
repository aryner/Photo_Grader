/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
	$(':submit[value=Submit]').on('click',function(e) {
		e.preventDefault();

		var maxCount = +($('input[type=hidden][name=maxCount]').val());
		var actualCount = maxCount;
		var focus = false;
		var names = [];

		for(var i=0; i<maxCount; i++) {
			var name = $('input[type=text][name=name'+i+']').val();
			if(invalidName(name, i)) {
				e.preventDefault();
			}

			if (!contains(names,name)) { $('div[name=sameName'+i+']').addClass('hidden'); }
			if(name.trim() === '') { $('div[name=error'+i+']').addClass('hidden'); actualCount--; continue; }

			if(contains(names,name)) {
				e.preventDefault();
				$('div[name=sameName'+i+']').removeClass('hidden');
				focus = setFocus($('input[name=name'+i+']'), focus);
			}
			names.push(name);

			var type = $('input[name=type'+i+'][value=1]').prop('checked') 
				|| $('input[name=type'+i+'][value=2]').prop('checked')
				|| $('input[name=type'+i+'][value=3]').prop('checked');
			var collect = $('input[name=collect'+i+'][value=1]').prop('checked')
				|| $('input[name=collect'+i+'][value=2]').prop('checked')
				|| $('input[name=collect'+i+'][value=3]').prop('checked')
				|| $('input[name=collect'+i+'][value=4]').prop('checked');

			if(!type || !collect) {
				e.preventDefault();
				$('div[name=error'+i+']').removeClass('hidden');
				focus = setFocus($('input[name=name'+i+']'), focus);
			}
			else { $('div[name=error'+i+']').addClass('hidden'); }
		}
	});
});

function invalidName(name, index) {
	if(name.length > 0 && (!name.match(/^[a-zA-z]/) || name.match(/[^a-zA-z0-9_]/))) {
		$('span[name='+(index+1)+']').removeClass('hidden');
		return true;
	}
	$('span[name='+(index+1)+']').addClass('hidden');
	return false;
}

function setFocus(element, focus) {
	if(!focus) {
		element.focus();
		focus = true;
	}
	return focus;
}

function contains(haystack, needle) {
	for(var i=0; i<haystack.length; i++) {
		if(haystack[i].trim() === needle.trim()) {
			return true;
		}
	}
	return false;
}