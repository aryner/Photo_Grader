/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



$(document).ready(function() {
	$('input[type=text][name=userName]').on('input', function() {
		if(this.value.length > 30) {
			this.value = this.value.substring(0,30);
		}
	});
	$('input[name=password]').on('input', function() {
		if(this.value.length > 20) {
			this.value = this.value.substring(0,20);
		}
	});
});
