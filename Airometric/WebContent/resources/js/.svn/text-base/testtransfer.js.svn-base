	function validate(){
				
				var selectedTests = document.getElementById("transfertestnameSelect");
				var testnames = "";
				//return false
					for ( var m = 0; m < selectedTests.length; m++) {
						if (selectedTests[m].selected) {
							var testName = selectedTests[m].value;
							testnames = testnames+","+testName;
						}
					}
					 document.getElementById("transfertestname").value = testnames;
				document.getElementById("transferTestButton").click();
				return false;
			}