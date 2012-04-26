<?php

mysql_connect("localhost","root","sadgurugyan");
mysql_select_db("innobo");
$q=mysql_query("SELECT * FROM VoiceRuralCDN");
while($e=mysql_fetch_assoc($q))
$output[]=$e;
print(json_encode($output));
mysql_close();
?>

