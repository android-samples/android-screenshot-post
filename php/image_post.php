<?php
if (is_uploaded_file($_FILES["upfile"]["tmp_name"])) {
	// echo $_FILES["upfile"]["name"] . "=filename";
	if (move_uploaded_file($_FILES["upfile"]["tmp_name"], dirname(__FILE__)."/temp/" . $_FILES["upfile"]["name"])) {
		@chmod("temp/" . $_FILES["upfile"]["name"], 0644);
		echo "Upload done. File: {$_FILES['upfile']['name']}\n";
		exit;
	}
	else {
		echo "move_uploaded_file failed.";
		exit;
	}
}
?>
<form action="image_post.php" method="post" enctype="multipart/form-data">
	<div>
		<input type="file" name="upfile"/>
	</div>
	<div>
		<input type="submit" value="Upload" />
	</div>
</form>
