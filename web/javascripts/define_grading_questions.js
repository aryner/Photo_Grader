/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var questionCount = 0;

$(document).ready(function() {
	setTextLimit('name');

	addNewQuestionListener(0);
	addAnswerTypeListener(0);

	$(':submit[value=Submit]').click(function(e) {
		$('input[name=questionCount]').val(questionCount+1);

		var errors = getErrorMsg();
		console.log(errors.length);
		if(errors.length > 0) {
			console.log('error in grading questions');
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

function setTextLimit(name) {
	$('input[name='+name+']').on('input', function() {
		if(this.value.length > 30) {
			this.value = this.value.substring(0,30);
		}
	});
}

function getErrorMsg() {
	return checkForName(checkForFilledQuestions(checkGradeGroup()));
}

function checkForName(errors) {
	errors = errors || [];
	var usersInputName = $('input[name=name]').val().trim();
	if(usersInputName === '') errors.push("<p class='error'>You must enter a name for this grade category</p>");

	var usedNames = getUsedNames();
	if(arrayContains(usedNames,usersInputName)) errors.push("<p class='error'>That grading category name has already been used for this study</p>");

	return errors;
}

function checkGradeGroup() {
	var errors = [];
	var groupOptionCount = Number($('input[name=groupOptionCount]').val());
	var groupSelected = false;

	for(var i=-1; i<(groupOptionCount) && !groupSelected; i++) {
		if($('input[name=groupBy_'+i+']').prop('checked')) {
			groupSelected = true;
		}
	}
	if(!groupSelected) errors.push("<p class='error'>You must select how to group the pictures you will grade : 'Grade Pictures that share'</p>");

	return errors;
}

function checkForFilledQuestions(errors) {
	errors = errors || [];
	var incompleteQuestion = false;

	for(var i=0; i<=questionCount && !incompleteQuestion; i++) {
		incompleteQuestion = checkForIncompleteQuestion(i);
	}

	if(incompleteQuestion) errors.push("<p class='error'>You must fully fill out each question or uncheck some 'Ask Another Question?' boxes</p>");

	return errors;
}

function checkForIncompleteQuestion(index) {
	var result = checkForType(index) && checkForQuestion(index) && 
	       checkForOption(index) && checkForLabel(index) && 
	       checkConstraint(index) ? false : true;

        return result;
}

function checkConstraint(index) {
	var constraint = oneChecked($('input[name=constraints_'+index+']'));
	if (!constraint) return constraint;

	var constraintType;
	var radios = $('input[name=constraints_'+index+']');
	for (var i=0; i<radios.length; i++) {
		if(radios[i].checked) {
			constraintType = Number(radios[i].value);
		}
	}
	if (constraintType < 1) return constraint;
	//if constraintType == 1 then check for 'conditional on which?' etc...
	var result = checkForConstraints(index);

	return result;
}

function checkForConstraints(index) {
	var radios = $('input[name=dependent_'+index+']');
	var checkedRadio = null;
	for (var i=0; i<radios.length; i++) {
		if(radios[i].checked) checkedRadio = radios[i].value;
	}
	if (checkedRadio === null) {
		return false;
	}

	var constraintIndex = Number(checkedRadio.substr(checkedRadio.indexOf("_")+1));
	var constraintRadios = $('input[name=type_'+constraintIndex+']');
	var constraintType = 2;
	for (var i=0; i<constraintRadios.length; i++) {
		if(constraintRadios[i].checked)
			constraintType = Number(constraintRadios[i].value);
	}

	if (constraintType > 1) {
		var result1 = checkConditionalOnRadioCheck(index,constraintIndex);
		return result1;
	}
	var result = checkConditionalOnText(index,constraintIndex);

	return result;
}

function checkConditionalOnRadioCheck(index,constraintIndex) {
	var atLeastOneChecked = false;
	var i = 0;
	var option = $('input[name=constraint_option_'+index+'_'+i+']')[0];

	while (option !== undefined && !atLeastOneChecked) {
		if(option.checked) atLeastOneChecked = true;

		i++;
		option = $('input[name=constraint_option_'+index+'_'+i+']')[0];
	}
	var result = atLeastOneChecked;

	return result;
}

function checkConditionalOnText(index,constraintIndex) {
	var constraintTypes = $('input[name=text_option_'+constraintIndex+']');
	var constraintType;
	for (var i=0; i<constraintTypes.length; i++) {
		if (constraintTypes[i].checked)
			constraintType = constraintTypes[i].value;
	}
	
	if(constraintType === 'text') {
		var radios = $('input[name=constraint_text_0_'+index+']');

		return $('input[name=constraint_text_'+index+']').val().length > 0 && oneChecked(radios);
	}
	else {
		return $('input[name=constraint_from_'+index+']').val().length > 0 &&
			$('input[name=constraint_to_'+index+']').val().length > 0;
	}
}

function checkForLabel(index) {
	return $('input[name=label_'+index+']').val().length > 0;
}

function checkForType(index) {
	return oneChecked($('input[name=type_'+index+']'));
}

function oneChecked(radios) {
	for(var i=0; i<radios.length; i++) {
		if(radios[i].checked) return true;
	}

	return false;
}

function checkForQuestion(index) {
	return $('textarea[name=question_'+index+']').val().length > 0;
}

function checkForOption(index) {
	var checked = getCheckedRadio('input[name=type_'+index+']');
	if(checked.value === '1') {
		var radios = $('input[name=text_option_'+index+']');

		for(var i=0; i<3; i++) {
			if(radios[i].checked) return true;
		}
		return false;
	}
	else {
		return $('input[name=option_'+index+'_'+3+']').val() !== undefined;
	}
}

function getUsedNames() {
	var usedNamesCount = Number($("input[name='used_count']").val());
	var usedNames = [];
	for (var i=0; i<usedNamesCount; i++) {
		usedNames.push($('input[name=used_'+i+']').val());
	}
	return usedNames;
}

function addNewQuestionListener(index) {
	document.addEventListener('question'+index, newQuestionListener, false);
	$('input[name=new_question_'+index+']').change(function() {
		var event = new CustomEvent("question"+index, {'detail':{'index':index, 'checked':this.checked}});
		document.dispatchEvent(event);
	});
}

function addAnswerTypeListener(index) {
	document.addEventListener('answerType'+index, newAnswerTypeListener, false);
	var radios = $('input[name=type_'+index+']');
	for(var i=0; i<radios.length; i++) {
		radios[i].onclick = function() {
			var event = new CustomEvent("answerType"+index, {'detail':{'index':index, 'type':this.value}});
			document.dispatchEvent(event);
		};
	}
}

function addConditionalListener(index) {
	//name = constraints_index
	document.addEventListener('condition'+index, newConditionListener, false);
	var radios = $('input[name=constraints_'+index+']');
	for(var i=0; i<radios.length; i++) {
		radios[i].onclick = function() {
			var event = new CustomEvent('condition'+index, {'detail':{'index':Number(index), 'checked':this.checked, 'type':Number(this.value)}});
			document.dispatchEvent(event);
		};
	}
}

function addConditionedOnSelectedListener(event, conditionedOn) {
	var radios = document.getElementsByName("dependent_"+event.detail.index);
	for(var i=0; i<radios.length; i++) {
		radios[i].onchange = function() {
			var constraint_index = Number(this.value.substring(this.value.indexOf("_")+1));
			var type_radios = document.getElementsByName('type_'+constraint_index);
			for(var i=0; i<type_radios.length; i++) {
				if(type_radios[i].checked) {
					if(type_radios[i].value > 1) {
						//check/radio
						generateConstraintOptions(event.detail.index, constraint_index);
					}
					else {
						//text
						gennerateConstraintRange(event.detail.index, constraint_index);
					}
				}
			}
		};
	}
}

function addOtherTextListener(index, count) {
	var check = document.getElementById('other_text_'+index+'_'+count);
	check.onchange = function() {
		if(this.checked) {
			//add new text constraint
		}
		else {
			//remove any text restraints after this one
		}
	};
}

function generateConstraintOptions(index, constraint_index) {
	var option_count = Number(document.getElementsByName('option_count_'+constraint_index)[0].value)-1;
	var html = "<h4>Check options conditional for this question</h4>";
	for(var i=0; i<option_count; i++) {
		var option = document.getElementsByName('option_'+constraint_index+'_'+(i+1))[0];
		html += "<input type='checkbox' name='constraint_option_"+index+"_"+i+"' value='"+option.name+"'>"+option.value+"<br>";
	}

	document.getElementsByName("constraint_options_"+index)[0].innerHTML = html;
}

function gennerateConstraintRange(index, constraint_index) {
	var type = document.getElementsByName('text_option_'+constraint_index);
	for(var i=0; i<3; i++) {
		if(type[i].checked) {
			type = type[i].value;
			break;
		}
	}

	if(type === 'text') {
		var html = "<div id='text_0'><h4>Describe how this question is conditional</h4>"+
			   "exactly<input type='radio' name='constraint_text_0_"+index+"' value='exactly'>"+
			   "contains<input type='radio' name='constraint_text_0_"+index+"' value='contains'>&nbsp&nbsp&nbsp"+
			   "case sensitive?<input type='checkbox' name='constraint_case_0_"+index+"' value='case_sensitive'>"+
		   	   "<br><br><input type='text' name='constraint_text_"+index+"'>"+
			   "</div>";
		document.getElementsByName("constraint_options_"+index)[0].innerHTML = html;
		//use function to add listener for other possible conditionals
		// addOtherTextListener(index, 0);
	}
	else {
		var html = "<h4>Enter the range conditional for this question</h4>"+
		       "<input type='text' name='constraint_from_"+index+"'> - "+
		       "<input type='text' name='constraint_to_"+index+"'>";

		document.getElementsByName("constraint_options_"+index)[0].innerHTML = html;
		
		if(type === 'int') {
			intsOnlyEnforcer(document.getElementsByName('constraint_from_'+index)[0]);
			intsOnlyEnforcer(document.getElementsByName('constraint_to_'+index)[0]);
		}
		else {
			numsOnlyEnforcer(document.getElementsByName('constraint_from_'+index)[0]);
			numsOnlyEnforcer(document.getElementsByName('constraint_to_'+index)[0]);
		}
	}
}

var newQuestionListener = function(event) {
	if(event.detail.checked) {
		questionCount++;
		addQuestion(questionCount);
	}
	else {
		//delete all Qs below this one
		trimQuestions(event.detail.index);
		questionCount = event.detail.index;
	}
};

var newAnswerTypeListener = function(event) {
	if(event.detail.type === '1') {
		$('input[name=option_count_'+event.detail.index+']').val(0);
		var container = $('div[name=options_'+event.detail.index+']');
		container.empty();
		var contents = '<h4>Select What Type of Data is Accepted</h4>'+
				'<input type="radio" name="text_option_'+event.detail.index+'" value="text"> Text'+
				'<input type="radio" name="text_option_'+event.detail.index+'" value="int"> Integer'+
				'<input type="radio" name="text_option_'+event.detail.index+'" value="dec"> Decimal';
		container.append(contents); 
	}
	else {
		var container = $('div[name=options_'+event.detail.index+']');
		var optionCount = $('input[name=option_count_'+event.detail.index+']').val();

		if(Number(optionCount) === 0) {
			$('input[name=option_count_'+event.detail.index+']').val('1');
			container.empty();
			var contents = '<h4>Enter options for this question (at least 2)</h4><input type="text" name="option_'+event.detail.index+'_'+Number(optionCount+1)+'">'+
				       '<input type="radio" name="default_'+event.detail.index+'" value="'+Number(optionCount)+'"> Default?';
			container.append(contents);
			addOptionListener(event.detail.index, 1);
		}
	}
};

var newConditionListener = function(event) {
	if(event.detail.checked && event.detail.type > 0) {
		//add html to get information to make this a conditional question
		var conditionedOn = getConditionedOnOptions(event.detail.index);
		var html = "<div class='meta-col'><h4>Conditional on which?</h4>";
		for(var i=0; i<conditionedOn.length; i++) {
			html += '<input type="radio" name="dependent_'+event.detail.index+'" value="'+conditionedOn[i].attr('name')+'">';
			html += ' <span id="con_'+event.detail.index+'_'+i+'">'+conditionedOn[i].val()+'</span> ';
		}
		html += "</div><div class='meta-col' name='constraint_options_"+event.detail.index+"'></div>";

		document.getElementById('conditioned_'+event.detail.index).innerHTML = html;
		addLabelChangeListeners(event, conditionedOn);
		addConditionedOnSelectedListener(event, conditionedOn);
	}
	else {
		//remove html from previously checked condition
		// (?) and remove event listener?
		document.getElementById('conditioned_'+event.detail.index).innerHTML = "";
	}
};

function getConditionedOnOptions(index) {
	var names = [];
	for(var i=0; i<index; i++) {
		names.push($('input[name=label_'+i+']'));
	}

	return names;
}

function addLabelChangeListeners(event, conditionedOn) {
	for(var i=0; i<conditionedOn.length; i++) {
		conditionedOn[i].on('input', function () {
			var span = document.getElementById('con_'+event.detail.index+'_'+this.name.substring(this.name.indexOf("_")+1));
			span.innerHTML = this.value;
		});
	}
}

function addOptionListener(questionIndex, optionIndex) {
	$('input[name=option_'+questionIndex+'_'+optionIndex+']').on('input',function(e){
		var optionCount = $('input[name=option_count_'+questionIndex+']').val();
		if(this.value !== '' > 0 && Number(optionCount) === Number(optionIndex)) {
			addOption(questionIndex, optionIndex);
		}
		else if(this.value === '') {
			clearOptions(questionIndex, optionIndex, optionCount);
		}
	});
}

function addOption(questionIndex, optionIndex) {
	$('input[name=option_count_'+questionIndex+']').val(optionIndex+1);
	var container = $('div[name=options_'+questionIndex+']');

	var contents = '<input type="text" style="margin-left:5px;" name="option_'+questionIndex+'_'+(optionIndex+1)+'">'+
		       '<input type="radio" name="default_'+questionIndex+'" value="'+optionIndex+'"> <span id="span_'+questionIndex+'_'+optionIndex+'"> Default?</span>';
	container.append(contents);
	addOptionListener(questionIndex, optionIndex+1);
}

function clearOptions(questionIndex, optionIndex, optionCount) {
	for(var i=optionCount; i>optionIndex; i--) {
		$('input[name=option_'+questionIndex+'_'+i+']').remove();
		$('input[name=default_'+questionIndex+'][value='+(i-1)+']').remove();
		$('#span_'+questionIndex+'_'+(i-1)).remove();
	}

	$('input[name=option_count_'+questionIndex+']').val(optionIndex);
}

function trimQuestions(index) {
	for(var i=questionCount; i>index; i--) {
		$('div[name=index_'+i+']').remove();
	}
}

function getCheckedRadio(search) {
	var radios = $(search);
	for(var i=0; i<radios.length; i++) {
		if(radios[i].checked) return radios[i];
	}
	return null;
}

function addQuestion(index) {
	var newQuestion = '<div name="index_'+index+'">'+
			'<h3>Question '+(index+1)+'</h3>'+
			'<div class="meta-col">'+
				'<h4>Question label</h4>'+
				'<input type="text" name="label_'+index+'">'+
			'</div>'+
			'<div class="meta-col">'+
				'<h4>Answer type</h4>'+
				'<input type="radio" name="type_'+index+'" value="2"> Radio '+
				'<input type="radio" name="type_'+index+'" value="3"> Check box'+
				'<input type="radio" name="type_'+index+'" value="1"> Text box'+
			'</div>'+
			'<div class="meta-col">'+
				'<h4>Question</h4>'+
				'<textarea name="question_'+index+'" cols="20" rows="1"></textarea>'+
			'</div>'+
			'<input type="hidden" name="option_count_'+index+'" value="0">'+
			'<div class="meta-col" name="options_'+index+'">'+
			'</div><div class="newRow"></div>'+
			'<div class="meta-col">'+
				'<h4>Constraints?</h4>'+ 
				'<input type="radio" name="constraints_'+index+'" value="0"> Mandatory'+
				'<input type="radio" name="constraints_'+index+'" value="-1"> Optional'+
				'<input type="radio" name="constraints_'+index+'" value="1"> Conditional'+
			'</div>'+
			'<div id="conditioned_'+index+'"></div>'+
			'<div class="newRow"></div>'+
			'<br><br><br><input type="checkbox" name="new_question_'+index+'"> Ask Another Question?'+
			'</div><div class="newRow"></div></div> ';

	$("div[name=generated_sections]").append(newQuestion);

	addNewQuestionListener(index);
	addAnswerTypeListener(index);
	addConditionalListener(index);
}

function arrayContains(array, string) {
	for (var i=0; i<array.length; i++) {
		if(array[i] === string) return true;
	}
	return false;
}

function intsOnlyEnforcer(input) {
	input.oninput = function() {
		this.value = this.value.replace(/[^0-9]/,'');
	};
}

function numsOnlyEnforcer(input) {
	input.oninput = function() {
		while (this.value.length > 0 && !this.value.match(/^[0-9]+[\\\.]?[0-9]*$/,'')) {
			this.value = this.value.substring(0,this.value.length-1);
		}
	};
}