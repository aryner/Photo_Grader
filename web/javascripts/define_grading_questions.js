/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var questionCount = 0;

$(document).ready(function() {
	addNewQuestionListener(0);
	addAnswerTypeListener(0);

	$(':submit[value=Submit]').click(function(e) {
		$('input[name=questionCount]').val(questionCount+1);

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
	});
});

function getErrorMsg() {
	return checkForName(checkForFilledQuestions(checkGradeGroup()));
}

function checkForName(errors) {
	errors = errors || [];
	if($('input[name=name]').val() === '') errors.push("<p class='error'>You must enter a name for this grade category</p>");

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
	return checkForType(index) && checkForQuestion(index) && 
	       checkForOption(index) && checkForLabel(index) && 
	       checkConstraint(index) ? false : true;
}

function checkConstraint(index) {
	return oneChecked($('input[name=constraints_'+index+']'));
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
	console.log(checked.value);
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
			var contents = '<h4>Enter options for this question (at least 2)</h4><input type="text" name="option_'+event.detail.index+'_'+Number(optionCount+1)+'">';
			container.append(contents);
			addOptionListener(event.detail.index, 1);
		}
	}
};

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

	var contents = '<input type="text" style="margin-left:5px;" name="option_'+questionIndex+'_'+(optionIndex+1)+'">';
	container.append(contents);
	addOptionListener(questionIndex, optionIndex+1);
}

function clearOptions(questionIndex, optionIndex, optionCount) {
	for(var i=optionCount; i>optionIndex; i--) {
		$('input[name=option_'+questionIndex+'_'+i+']').remove();
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
				'<!-- <input type="radio" name="constraints_'+index+'" value="?"> Conditional -->'+
			'</div>'+
			'<div class="newRow"></div>'+
			'<br><br><br><input type="checkbox" name="new_question_'+index+'"> Ask Another Question?'+
			'</div><div class="newRow"></div></div> ';

	$("div[name=generated_sections]").append(newQuestion);

	addNewQuestionListener(index);
	addAnswerTypeListener(index);
}