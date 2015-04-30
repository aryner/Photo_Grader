/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var questionCount = 0;

$(document).ready(function() {
	addNewQuestionListener(0);
	addAnswerTypeListener(0);
});

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
	if(event.detail.type === 'text') {
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

		if((+optionCount) === 0) {
			$('input[name=option_count_'+event.detail.index+']').val(1);
			container.empty();
			var contents = '<h4>Enter options for this question</h4><input type="text" name="option_'+event.detail.index+'_'+(optionCount+1)+'">';
			container.append(contents);
		}
	}
};

function trimQuestions(index) {
	for(var i=questionCount; i>index; i--) {
		$('div[name=index_'+i+']').remove();
	}
}

function addQuestion(index) {
	var newQuestion = '<div name="index_'+index+'">'+
			'<div class="meta-col">'+
				'<h4>Answer type</h4>'+
				'<input type="radio" name="type_'+index+'" value="radio"> Radio '+
				'<input type="radio" name="type_'+index+'" value="checkbox"> Check box'+
				'<input type="radio" name="type_'+index+'" value="text"> Text box'+
			'</div>'+
			'<div class="meta-col">'+
				'<h4>Question</h4>'+
				'<textarea name="question_'+index+'" cols="40" rows="1"></textarea>'+
			'</div>'+
			'<input type="hidden" name="option_count_'+index+'" value="0">'+
			'<div class="meta-col" name="options_'+index+'">'+
			'</div>'+
			'<div class="newRow"></div>'+
			'<br><br><br><input type="checkbox" name="new_question_'+index+'"> Ask Another Question?'+
			'<div class="newRow"></div></div> ';

	$("div[name=generated_sections]").append(newQuestion);

	addNewQuestionListener(index);
	addAnswerTypeListener(index);
}