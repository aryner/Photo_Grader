/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function() {
	$(':submit[value=Submit]').on('click',function(e) {
		e.preventDefault();
		var maxCount = +($('input[type=hidden][name=maxCount]').val());
		var focus = false;
		var names = [];

		for(var i=0; i<maxCount; i++) {
			var name = $('input[type=text][name=name'+i+']').val();

			if(errorsCheck(name, names, i)) {
				e.preventDefault();
				focus = setFocus($('input[name=name'+i+']'), focus);
			}
			if(name.trim() !== '') names.push(name);
		}
	});
});

function errorsCheck(name, names, index) {
	return (invalidName(name, index) || repeatName(names, name, index) || incompleteRow(name, index));
}

function incompleteRow(name, index) {
	var type = $('input[name=type'+index+'][value=1]').prop('checked') 
		|| $('input[name=type'+index+'][value=2]').prop('checked')
		|| $('input[name=type'+index+'][value=3]').prop('checked');

	var collect = $('input[name=collect'+index+'][value=1]').prop('checked')
		|| $('input[name=collect'+index+'][value=2]').prop('checked')
		|| $('input[name=collect'+index+'][value=3]').prop('checked')
		|| $('input[name=collect'+index+'][value=4]').prop('checked');

	if(name !== '' && (!type || !collect)) {
		$('div[name=error'+index+']').removeClass('hidden');
		return true;
	}
	$('div[name=error'+index+']').addClass('hidden'); 
	return false;
}

function invalidName(name, index) {
	if(name.length > 0 && (!name.match(/^[a-zA-z]/) || name.match(/[^a-zA-Z0-9]/))) {
		$('span[name='+(index+1)+']').removeClass('hidden');
		return true;
	}
	$('span[name='+(index+1)+']').addClass('hidden');
	return false;
}

function repeatName(names,name, index) {
	if(contains(names,name)) {
		$('div[name=sameName'+index+']').removeClass('hidden');
		return true;
	}
	$('div[name=sameName'+index+']').addClass('hidden'); 

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