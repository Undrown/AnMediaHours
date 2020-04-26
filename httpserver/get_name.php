<?php 
$fp = fopen("Users", "r");
$data = "ERROR_INVALID_PHONE_NUMBER";
while($line = fgets($fp)){
	$array = preg_split("/ -- /", $line);
	if($_GET['phone'] == $array[2]){
		$data = $line;
	}
}
fclose($fp);
echo rtrim($data, "\n");
?>