<?php
$result = "OK";
//uid=...&time_start=...&time_end=...&comment=...
$uid = (int)$_GET['uid'];
$comment = $_GET['comment'];
$l1 = (float)$_GET['time_start'];
$l2 = (float)$_GET['time_end'];
if(preg_match("/\n/", $comment)){
	echo "ERROR_NEW_LINE_AT_COMMENT";
	die();
}
if($l1 > $l2){
	echo "ERROR_NEGATIVE_TIME_PERIOD";
	die();
}
if($uid == 0){
	echo "ERROR_INVALID_UID";
	die();
}
//finally, if $result is still "OK" :
if($result == "OK"){
	$data = "00 -- $uid -- $l1 -- $l2 -- $comment\n";
    $fp = fopen("Data", "a");
    fwrite($fp, $data);
    fclose($fp);
    echo $result;
}
?>