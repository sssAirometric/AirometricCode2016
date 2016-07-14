//Please wait box
var messageBoxDIV = document.getElementById("messageBox");
var overlayElem = document.getElementById("overlay");
var messageOverlayElem = document.getElementById("messageOverlay");
var pleaseWaitDIV = document.getElementById("wait");
var pleaseWaitFrame = document.getElementById("waitFrame");
//start and end timers...may not be needed anymore.
var startTime=0;
var endTime=0;

//disable all ddlb's for background overlay.
function alterSelects(stat) {
	var selectListBoxes=document.getElementsByTagName("select");
	for(var r=0;r<selectListBoxes.length;r++)
		selectListBoxes[r].disabled=stat;
}

function pleaseWait(pwOn, bgFade) {
	if(pwOn) {
		if(startTime==0) startTime = (new Date()).getTime();
		//alterSelects(true);
		showWait(bgFade);
		
	} else {
		//alterSelects(false);
		closeWait();
		endTime=(new Date()).getTime();
		showTime(startTime, endTime);
		startTime=0;
		endTime=0;
	}
}

function showTime(sTime, eTime) {
	var elapsedTime=eTime-sTime;
	var seconds = elapsedTime/1000;
	//alert("Time: " + seconds);
}

function showWait(bgFade) {
	if(bgFade) showOverlay(overlayElem);
	
	pleaseWaitFrame.style.display = "inline";
	pleaseWaitDIV.style.display = "inline";
	
	pleaseWaitFrame.style.width=pleaseWaitDIV.offsetWidth;
	pleaseWaitFrame.style.height=pleaseWaitDIV.offsetHeight;
	centerObj(pleaseWaitFrame);
	
	centerObj(pleaseWaitDIV);
	
	document.body.style.cursor = "wait";
	setTimeout('document.images["pleaseWait"].src = "../../Airometric/resources/images/loading_small.gif"', 200);
}

function closeWait() {
	pleaseWaitDIV.style.display="none";
	pleaseWaitFrame.style.display="none";
	hideOverlay(overlayElem);
	document.body.style.cursor = "default";
}

function showOverlay(ovrlay) {
	ovrlay.style.display="inline";
	ovrlay.style.height = screen.height;
	ovrlay.style.width = screen.width;
	centerObj(ovrlay);
}
function hideOverlay(ovrlay) { ovrlay.style.display="none"; }


function centerObj(obj) {

	obj.style.position = "absolute";

	var objLeft = (screen.width - obj.offsetWidth) / 2;
	var objTop = (screen.height - obj.offsetHeight) / 2;
	objLeft = objLeft + document.body.scrollLeft;
	objTop = objTop + document.body.scrollTop;

	obj.style.top = objTop;
	obj.style.left = objLeft;
	
	//alert("ID: " + obj.id + "\nWidth: " + obj.offsetWidth + "\nHeight: " + obj.offsetWidth + "\nTop: " + objTop);
}


function getCurrentTime() {
	var rightNow = new Date();
	
	// Time must be in 15 minute increments...
	var minutes = "00";
	var curMinutes = rightNow.getMinutes();
	if (curMinutes > 7 && curMinutes < 21) {
		minutes = "15";
	}
	else if (curMinutes > 22 && curMinutes < 37) {
		minutes = "30";
	}
	else if(curMinutes > 38 && curMinutes < 52) {
		minutes = "45";
	}
	else {
		// since minutes are > 52 we are setting them to 00 increment the hour
		// ex. if current time is 8:53am we will set minutes to 00 and hour to 9 for the result of 09:00
		hours += 1;
	}
	//Hours must be zero padded.
	var hours = rightNow.getHours();
	if(hours < 10) hours = "0" + hours;
	
	return hours + ":" + minutes;
}

function getCurrentDate() {
	var date = new Date();
	
	var month = date.getMonth() + 1;
	// month must be zero padded
	if (month < 10) month = "0" + month;
	
	var day = date.getDate();
	// day must be zero padded.
	if (day < 10) day = "0" + day;
	
	return month + "/" + day + "/" + date.getFullYear();
}

function showMessageBox(msg, closeFlag, focusField, width, height,searchDiv) {
	if(msg==undefined || msg=="") return;

	showClose=true;
	if(closeFlag==undefined || closeFlag=="" || closeFlag.toLowerCase()=="true" || closeFlag)
		showClose=false;
	
	messageBoxDIV.style.display="block";
	
	if(width!=undefined) messageBoxDIV.style.width=width;
	if(height!=undefined) messageBoxDIV.style.height=height;
	
	document.getElementById("messageBoxMessage").innerHTML = msg;
	if(showClose) 
		document.getElementById("messageBoxClose").style.display="inline";
	else
		document.getElementById("messageBoxClose").style.display="none";
	
	
	if(focusField!=undefined && focusField!="") {
		//For some reason this doesn't always work...
		document.getElementById(focusField).focus();
	}
	
	if(searchDiv){
		rightObj(messageBoxDIV);
	}else{
		centerObj(messageBoxDIV);
		
		pleaseWaitFrame.style.display="block";
		pleaseWaitFrame.style.width=messageBoxDIV.offsetWidth;
	
		pleaseWaitFrame.style.height=messageBoxDIV.offsetHeight;
		centerObj(pleaseWaitFrame);
		showOverlay(messageOverlayElem);
	}
}

function closeMessageBox(searchDiv) {
	messageBoxDIV.style.display="none";
	if(!searchDiv){
		messageOverlayElem.style.display="none";
		pleaseWaitFrame.style.display="none";
	}
}


//Modified for searchDiv
function rightObj(obj) {

	obj.style.position = "absolute";

	var objLeft = (screen.width - obj.offsetWidth) / 5;
	var objTop = (screen.height - obj.offsetHeight) / 5;
	objLeft = objLeft + document.body.scrollLeft;
	objTop = objTop + document.body.scrollTop;

	obj.style.bottom = objTop;
	obj.style.right = objLeft+30;
	
	//alert("ID: " + obj.id + "\nWidth: " + obj.offsetWidth + "\nHeight: " + obj.offsetWidth + "\nTop: " + objTop);
}