const toggleSidebar=()=>{
console.log("this is script")
	if($(".sidebar").is(":visible")){
	/*	true
		band krna hai*/
		$(".sidebar").css("display","none");
		
		
	}else{
		/*false
		show krna hai*/
		$(".sidebar").css("display","block");
		
	}
}