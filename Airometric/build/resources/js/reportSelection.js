var marketNames = new Array();
var allMarketsNames = "";
var marketIndex = 0;
var allMarkets = new Array();
var allDevices = new Array();
var testNameArr = new Array();
var filesSelectedArr = new Array();
var compareCount = 0;
var uniqueMarket=new Array();
function checkMaxNumberMarkets() {
	//document.getElementById("buttonDiv").style.display="inline";
	var test=chkmrkts();
	//alert("Ytest--------------"+test);
	if (chkmrkts()) {
		alert("You can select only 4 markets");
		return false;
		
	}
	var marketNames = "";
	var numberNewMarkets = 0;
	var selectmarket = document.getElementById("marketId");
	var selectedDevice = document.getElementById("deviceName").value;
	var testCaseName = document.getElementById("testCaseName").value;
	for ( var m = 0; m < selectmarket.length; m++) {
		if (selectmarket[m].selected) {
			var marketName = selectmarket[m].value;
			if ($.inArray(marketName, allMarkets) < 0) {
				numberNewMarkets++;
			}
		
		}
	}
	
	if($.inArray(selectedDevice, allDevices) < 0){
		allDevices[allDevices.length] = selectedDevice;
	}
	if($.inArray(testCaseName, testNameArr) < 0){
		testNameArr[testNameArr.length] = testCaseName;
	}
	//alert("allMarkets------------+--------"+allMarkets.length);
	//alert("numberNewMarkets-------+-------------"+numberNewMarkets);
	var potentialArraySize = (allMarkets.length * 1) + (numberNewMarkets * 1);
	//alert("potentialArraySize-------+-------------"+potentialArraySize);

	
		if($.inArray(selectedDevice, allDevices) < 0){
			allDevices[allDevices.length] = selectedDevice;
		}
		
			for ( var m = 0; m < selectmarket.length; m++) {
				if (selectmarket[m].selected) {
					var marketName = selectmarket[m].value;
					//if ($.inArray(marketName, allMarkets) < 0) {
						if( !marketName.match("Select Market") ||!marketName=="" ){
							marketNames = marketNames+","+marketName;
							marketNames[marketNames.length] = marketName;
							allMarkets[allMarkets.length] = marketName;
						}else{
							marketNames = marketName;
							marketNames[marketNames.length] = marketName;
							allMarkets[allMarkets.length] = marketName;
						}
					//}
				}
			}
		//alert("selected markets-------------------------------"+marketNames);
//		alert("allMarkets-------------------------------------"+allMarkets);
		var employees = "[{ firstName:John , lastName:Doe }, { firstName:Anna , lastName:Smith }, { firstName:Peter , lastName: Jones }]";
		var finalJson = "{finalJso:" + employees + "}";
		document.getElementById("numberTimesCallMade").value = "0";
		document.getElementById("testSummaryComment").Value = "hiiiiiiiii";
		
		saveInConfigArr(allMarkets);
}


// THIS FUNCTION FORMS A JSON STRING FOR JSON OBJECT IN BEAN CLASS AND IN PUSHED
// INTO THE ARRAY
function saveInConfigArr(allMarkets) {
	//alert("saveInConfigArr function:");
	var deviceName = "";
	var testName = "";
	var marketName = "";
	var marketLabel = "";
	var reportType = "";
	var filesName = "";
	var selectmarket = null;
	var selectFiles = null;
	var deviceid="";
	var filesNameId = "";
	var numberOfMarket = 0;
	var numberOfFile = 0;
	var selectBoxMarketObj = document.getElementById("marketId");
	for(var i=0;i<selectBoxMarketObj.length;i++){
		var selectedObj = selectBoxMarketObj[i];
		if(selectedObj.selected){
			numberOfMarket++;
		}
	}
	var selectBoxFileObj = document.getElementById("filesIds");
	for(var i=0;i<selectBoxFileObj.length;i++){
		var selectedObj = selectBoxFileObj[i];
		if(selectedObj.selected){
			numberOfFile++;
		}
	}
	
	
	if(document.getElementById("deviceName").value=="Select Device"){
		alert("Please select device type.");
		return false;
	}else if(document.getElementById("testCaseName").value=="Select Test"){
		alert("Please select test name.");
		return false;
	}else if(document.getElementById("reportType").value=="Select Report"){
		alert("Please select report type.");
		return false;
	}else if(numberOfMarket==0){
		alert("Please select market place.");
		return false;
	}else if(numberOfFile==0){
		alert("Please select file.");
		return false;
	}else{
		deviceName = "\"deviceName\":" +"\""+document.getElementById("deviceName").value+"\"";
		testName = "\"testName\":"
			+"\""+ document.getElementById("testCaseName").value+"\"";	
		reportType = "\"reportType\":"
			+ document.getElementById("reportType").value;
		selectmarket = document.getElementById("marketId");
		selectFiles = document.getElementById("filesIds");
		deviceid=filesName.substring(filesName.lastIndexOf("_")+1, filesName.length-1);
		deviceid=document.getElementById("filesIds").value;		
		
		for ( var m = 0; m < selectmarket.length; m++) {
			if (selectmarket[m].selected) {
				marketName = marketName + "," + selectmarket[m].value;
				marketLabel=marketLabel + "," + selectmarket[m].text;
			}
		}
		for ( var m = 0; m < selectFiles.length; m++) {
			if (selectFiles[m].selected) {
				filesName = filesName + "," + selectFiles[m].label;
				filesNameId = filesNameId + "," + selectFiles[m].value;
				if($.inArray(selectFiles[m].value, filesSelectedArr) < 0){
					filesSelectedArr[filesSelectedArr.length] = selectFiles[m].label;
				}
			}
		}
		marketName = "\"marketName\":" + "["
					+ marketName.substring(1, marketName.length) + "]";
		var marketLabelName=marketLabel.substring(1, marketLabel.length);
		marketLabel = "\"marketLabel\":" + "["
		+"\""+ marketLabel.substring(1, marketLabel.length)+"\"" + "]";
		filesName = "\"filesName\":" + "["
		+"\""+ filesName.substring(1, filesName.length)+"\"" + "]";
		filesNameId = "\"filesNameId\":" + "["
					+ filesNameId.substring(1, filesNameId.length) + "]";
		//alert("filesName=="+filesName);	
		deviceid=filesName.substring(filesName.lastIndexOf("_")+1, filesName.length-2);
		
		var jsonStr = "{" + deviceName + "," + testName + "," + marketName + ","+ marketLabel + ","
		+ reportType + "," + filesName + "," + filesNameId +"}";
			if (configurationArray[0] == null || configurationArray[0].length==0) {
				if (configurationArray.indexOf(jsonStr) > -1) {
					alert("Please select different combination.");
				}else{
					document.getElementById("compareDiv_0").innerHTML =  document.getElementById("deviceName").value+"<br/>"+
					document.getElementById("testCaseName").value+"<br/>"+ marketLabelName +"<br/>"+
					deviceid+
					"<a href='javascript:void(0);removeFromDiv(0)' class='remove'>X</a> ";
					configurationArray[0] = jsonStr;
				}
			} else if (configurationArray[1] == null|| configurationArray[1].length==0) {
				if (configurationArray.indexOf(jsonStr) > -1) {
					alert("Please select different combination.");
				}else{
					document.getElementById("compareDiv_1").innerHTML =  document.getElementById("deviceName").value+"<br/>"+
					document.getElementById("testCaseName").value+"<br/>"+ marketLabelName+"<br/>"+
					deviceid+
					"<a href='javascript:void(0);removeFromDiv(1)' class='remove'>X</a> ";
					configurationArray[1] = jsonStr;
					//alert("2."+configurationArray[1]);
				}
			} else if (configurationArray[2] == null || configurationArray[2].length==0) {
				if (configurationArray.indexOf(jsonStr) > -1) {
					alert("Please select different combination.");
				}else{
					configurationArray[2] = jsonStr;
					document.getElementById("compareDiv_2").innerHTML =  document.getElementById("deviceName").value+"<br/>"+
					document.getElementById("testCaseName").value+"<br/>"+ marketLabelName+"<br/>"+
					deviceid+
					"<a href='javascript:void(0);removeFromDiv(2)' class='remove'>X</a> ";
					//alert("3."+configurationArray[2]);
				}
			} else if (configurationArray[3] == null || configurationArray[3].length==0) {
				if (configurationArray.indexOf(jsonStr) > -1) {
					alert("Please select different combination.");
				}else{
					configurationArray[3] = jsonStr;
					document.getElementById("compareDiv_3").innerHTML =  document.getElementById("deviceName").value+"<br/>"+
					document.getElementById("testCaseName").value+"<br/>"+ marketLabelName+"<br/>"+
					deviceid+
					"<a href='javascript:void(0);removeFromDiv(3)' class='remove'>X</a> ";
					//alert("4."+configurationArray[3]);
				}
			} else{
				alert("Maximum allowed rounds are 4 for compare.");
			}
	}		
}

function formJsonBeforeSubmit(){
	var finalJsonStr = "";
	var finalJsonArr = "";
		for(var m=0;m<configurationArray.length;m++){
			if(configurationArray[m]!=null){
				finalJsonStr = finalJsonStr+","+configurationArray[m];
				//alert(m+":=============**========"+ configurationArray[m]);
			}
		}
			
		finalJsonArr = "["+finalJsonStr.substring(1, finalJsonStr.length)+"]";
		
		finalJsonStr =  "{finalJso:" + finalJsonArr + "}";
		//alert("finalJsonStr-------------"+finalJsonStr);
		document.getElementById("configJsonArray").value = finalJsonStr;
		document.getElementById("reportingParams").value = finalJsonStr;
		//alert("allDevices------1------"+allDevices);
		if(dlist.length>0){
			if(dlist==allDevices){
				allDevices=allDevices;
			}else{
				allDevices=dlist+","+allDevices;
			}
		}
		if(tstnamelist.length>0){
			if(tstnamelist==testNameArr){
				testNameArr=testNameArr;
			}else{
				testNameArr=tstnamelist+","+testNameArr;
			}
		}
		if(filnamelist.length>0){
			if(filnamelist==filesSelectedArr){
				filesSelectedArr=filesSelectedArr;
			}else{
				filesSelectedArr=filnamelist+","+filesSelectedArr;
			}
		}
		if(mlist.length>0){
			if(mlist==allMarkets){
				 allMarkets=allMarkets;
			}else{
				
				 allMarkets=mlist+","+allMarkets;				
			}
		}
		document.getElementById("allMarkets").value = allMarkets;
		document.getElementById("allDevices").value = allDevices;
		document.getElementById("hiddenTestcaseArr").value = testNameArr;
		document.getElementById("filesSelectedArr").value = filesSelectedArr;
		//alert("allMarkets-----**********--------final----------"+allMarkets);
		if(isDownload.match("false")){
			pleaseWait(true, true);
		}
}

function assignDivValue(divId,deviceName,testName,marketName,imeinum,val){
	 document.getElementById(divId).innerHTML = deviceName+"<br/>"+testName+"<br/>"+marketName+"<br/>"+imeinum+"<a href='javascript:void(0);removeFromDiv("+val+")' class='remove'>X</a> ";
}



function chkmrkts(){
	var selectmarket = document.getElementById("marketId");
	var uniqueMarketSize = uniqueMarket.length;
	var tempMarketArr = new Array();
	var numberNewMarkets = 0;
	//alert("uniqueMarketSize===="+uniqueMarketSize);
	for ( var m = 0; m < allMarkets.length; m++) {
			var marketName = allMarkets[m];
			if ($.inArray(marketName, tempMarketArr) < 0) {
				tempMarketArr[tempMarketArr.length] = marketName;
				//numberNewMarkets++;
			}
	}
	for ( var m = 0; m < selectmarket.length; m++) {
		if (selectmarket[m].selected) {
			var marketName = selectmarket[m].value;
			if ($.inArray(marketName, allMarkets) < 0) {
				numberNewMarkets++;
			}
		
		}
	}
	uniqueMarketSize = (tempMarketArr.length*1) + numberNewMarkets*1;
	if(uniqueMarketSize>4){
		return true;
	}
	else{
		return false;
	}
}