/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
	$('input[type=text][name=number]').on('input', function() {
		this.value = this.value.replace(/[^0-9]/g,'');
	});
	$('input[type=text][name=name]').on('input', function() {
		if(this.value.length > 90) {
			this.value = this.value.substring(0,90);
		}
	});

	$(":submit[name=newStudy]").on('click',function(e){
		var name = $('input[type=text][name=name]').val();
		var number = $('input[type=text][name=number]').val();

		if(name.trim() === '' || number.trim() === '') {
			e.preventDefault();
			$('span[name=missingNameNumber]').removeClass('hidden');
		}
		if(studyNameTaken()) {
			e.preventDefault();
			$('span[name=repeatName]').removeClass('hidden');
		}
	});
});

function studyNameTaken() {
	var options = document.getElementById('studyOptions');
	var name = $('input[type=text][name=name]').val();

	for(var i=0; i<options.length; i++) {
		if(name.trim() === options[i].value) {
			return true;
		}
	}

	return false;
}