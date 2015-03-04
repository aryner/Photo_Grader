/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
	$('input[type=text][name=number]').on('input', function() {
		this.value = this.value.replace(/[^0-9]/g,'');
	});

	$(":submit[name=newStudy]").on('click',function(e){
		var name = $('input[type=text][name=name]').val();
		var number = $('input[type=text][name=number]').val();

		if(name.trim() === '' || number.trim() === '') {
			e.preventDefault();
			$('span[name=missingNameNumber]').removeClass('hidden');
		}
	});
});