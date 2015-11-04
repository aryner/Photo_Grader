/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
checked_groups = [];

$(document).ready(function() {
	setNextOptionEmitter();

	$(':submit[value=Submit]').click(function(e) {

		var errors = getErrorMsg();
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
	return checkForInvalidCharacters(checkForExtremes(checkCompareField(checkGradeGroup(checkForName()))));
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

function arrayContains(array, needle) {
	for (var i=0; i<array.length; i++) {
		if(array[i] === needle) return true;
	}
	return false;
}

function checkGradeGroup(errors) {
	var groupOptionCount = Number($('input[name=groupOptionCount]').val());
	var groupSelected = false;
	checked_groups = [];

	for(var i=0; i<(groupOptionCount); i++) {
		if($('input[name=groupBy_'+i+']').prop('checked')) {
			groupSelected = true;
			checked_groups.push(i);
		}
	}
	if(!groupSelected) errors.push("<p class='error'>You must select how to group the pictures you will rank: 'Rank Pictures that share'</p>");

	return errors;
}

function checkCompareField(errors) {
	var compareFields = document.getElementsByName('compare_between');
	var checked_index = -1;
	for(var i=0; i<compareFields.length; i++) {
		if (compareFields[i].checked) {
			if (arrayContains(checked_groups,i)) {
				errors.push('<p class="error">The selected compare field cannot be the same as any selected group by field</p>');
			}
			return errors;
		}
	}
	errors.push('<p class="error">You must select a field to compare within</p>');
	return errors;
}

function checkForExtremes(errors) {
	errors = checkForLowEnds(errors);
	return checkForHighEnds(errors);
}

function checkForLowEnds(errors) {
	var count = document.getElementsByName("low_count")[0].value;
	for(var i=0; i<=count; i++) {
		var input = document.getElementsByName("low_"+i)[0].value;
		if(input.trim() === '') {
			errors.push("<p class='error'>You must enter a value for each textbox</p>");
			return errors;
		}
	}

	return errors;
}

function checkForHighEnds(errors) {
	var count = document.getElementsByName("high_count")[0].value;
	for(var i=0; i<=count; i++) {
		var input = document.getElementsByName("high_"+i)[0].value;
		if(input.trim() === '') {
			errors.push("<p class='error'>You must enter a value for each textbox</p>");
			return errors;
		}
	}

	return errors;
}

function setNextOptionEmitter() {
	setNextHighOptionEmitter(0);
	setNextLowOptionEmitter(0);
}

function setNextHighOptionEmitter(index) {
	var check = document.getElementsByName('high_opt_'+index)[0];
	check.onclick = function() {
		var event = new CustomEvent('next_high_option'+index,{'detail':{'index':index,'checked':this.checked,'extreme':'high'}});
		document.dispatchEvent(event);
	};
	document.addEventListener('next_high_option'+index, next_option, false);
}

function setNextLowOptionEmitter(index) {
	var check = document.getElementsByName('low_opt_'+index)[0];
	check.onclick = function() {
		var event = new CustomEvent('next_low_option'+index,{'detail':{'index':index,'checked':this.checked,'extreme':'low','box':this}});
		document.dispatchEvent(event);
	};
	document.addEventListener('next_low_option'+index, next_option, false);
}

var next_option = function(event) {
	if(event.detail.checked) {
		addNextOptions(event);
	}
	else {
		clearNextOptions(event);
	}
};

function addNextOptions(event) {
	var row = document.getElementById(event.detail.extreme === 'low' ? 'low':'high');
	var newDiv = document.createElement('div');
	newDiv.className = "meta-col";
	newDiv.id = row.id+'_'+(+event.detail.index+1);

	if(row.id === 'low') {
		var h3 = document.createElement("H3");
		var dot = document.createTextNode(".");
		h3.className = 'white';
		h3.appendChild(dot);
		newDiv.appendChild(h3);
	}
	var h4 = document.createElement("H4");
	var text = document.createTextNode("Next ideal "+row.id+" end");
	h4.appendChild(text);
	newDiv.appendChild(h4);
	var input = document.createElement('input');
	input.type = 'text';
	input.name = row.id+'_'+(+event.detail.index+1);
	newDiv.appendChild(input);
	input = document.createElement('input');
	input.type='checkbox';
	input.name = row.id+'_opt_'+(+event.detail.index+1);
	newDiv.appendChild(input);

	row.appendChild(newDiv);

	var count = document.getElementsByName(row.id+"_count")[0];
	count.value = Number(count.value)+1;

	if(row.id === 'low') { setNextLowOptionEmitter(count.value); }
	else { setNextHighOptionEmitter(count.value); }
}

function clearNextOptions(event) {
	var index = Number(event.detail.index);
	var count = document.getElementsByName(event.detail.extreme+"_count")[0];
	count.value = index;

        index++;
	var div = document.getElementById(event.detail.extreme+'_'+index);
	while (div !== undefined) {
		div.parentNode.removeChild(div);
		index++;
		div = document.getElementById(event.detail.extreme+'_'+index);
	}
}
