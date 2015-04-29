/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var questionCount = 0;

$(document).ready(function() {
	$('input[name=new_question_0]').change(function() {
		if(this.checked) {
			questionCount++;
			addQuestion(questionCount);
		}
		else {
			//delete all Qs below this one
			trimQuestions(0);
			questionCount = 0;
		}
	});
});

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
			'<div class="meta-col" name="options_'+index+'">'+
			'</div>'+
			'<div class="newRow"></div>'+
			'<br><br><br><input type="checkbox" name="new_question_'+index+'"> Ask Another Question?'+
			'<div class="newRow"></div></div> ';

	$("div[name=generated_sections]").append(newQuestion);
}

function trimQuestions(index) {
	for(var i=questionCount; i>index; i--) {
		$('div[name=index_'+i+']').remove();
	}
}