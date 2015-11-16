/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
	$(document).bind('keydown',function(e) {
		var unicode = e.keyCode || e.which;
		//return 
		if(unicode === 13) { e.preventDefault(); }
	});
	$(':submit[value=Submit]').click(function(e) {

		var errors = getErrorMsg();
		console.log(errors.length);
		if(errors.length > 0) {
			e.preventDefault();
			var msg = "";
			for(var i=0; i<errors.length; i++) {
				msg += errors[i];
			}
			var div = document.getElementsByClassName('errorDiv');
			div[0].innerHTML = msg;
		}
		else {
			var div = document.getElementsByClassName('errorDiv');
			div[0].innerHTML = "";
		}
	});
});

function getErrorMsg() {
	return checkForInvalidCharacters(checkGradeGroup(checkForName()));
}

function checkForName(errors) {
	errors = errors || [];
	var usersInputName = $('input[name=name]').val().trim();
	if(usersInputName === '') errors.push("<p class='error'>You must enter a name for this grade category</p>");

	var usedNames = getUsedNames();
	if(arrayContains(usedNames,usersInputName)) errors.push("<p class='error'>That ranking category name has already been used for this study</p>");

	return errors;
}

function checkForInvalidCharacters(errors) {
	var inputs = $('input[type=text]');
	var textAreas = $('textarea');
	for (var i=0; i<textAreas.length; i++) {
		inputs.push(textAreas[i]);
	}
	for(var i=0; i<inputs.length; i++) {
		if(inputs[i].value.trim().match(/[^a-zA-Z0-9\s]/)){
			errors.push("<p class='error'>You can only use letters and numbers</p>");
			break;
		}
	}

	return errors;
}

function getUsedNames() {
	var usedNamesCount = Number($("input[name='used_count']").val());
	var usedNames = [];
	for (var i=0; i<usedNamesCount; i++) {
		usedNames.push($('input[name=used_'+i+']').val());
	}
	return usedNames;
}

function arrayContains(array, string) {
	for (var i=0; i<array.length; i++) {
		if(array[i] === string) return true;
	}
	return false;
}

function checkGradeGroup(errors) {
	var groupOptionCount = Number($('input[name=groupOptionCount]').val());
	var groupSelected = false;

	for(var i=-1; i<(groupOptionCount) && !groupSelected; i++) {
		if($('input[name=groupBy_'+i+']').prop('checked')) {
			groupSelected = true;
		}
	}
	if(!groupSelected) errors.push("<p class='error'>You must select how to group the pictures you will rank: 'Rank Pictures that share'</p>");

	return errors;
}