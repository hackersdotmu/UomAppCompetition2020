<?php 
    if(isset($_POST["orgId"])) {
      
        $file_name=($_POST['orgId']).".pdf";
        $target_dir = "C:/inetpub/wwwroot/appcup/newapi/datastore/pdf/";
        $target_file = $target_dir . $file_name;
        $uploadOk = 1;
        //$imageFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));
        
        if(isset($_POST["orgId"])) {
            //$check = getimagesize($_FILES["fileToUpload"]["tmp_name"]);
            // if($check !== false) {
            //     $uploadOk = 1;
            // } else {
            //     $uploadOk = 0;
            // }
        }
        if (file_exists($target_file)) {
            $uploadOk=0;
        }
        if ($_FILES["fileToUpload"]["size"] > 5000000) {
        
            $uploadOk = 0;
        }
    
      
    
        if ($uploadOk == 0) {
        
        } else {
            if (move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $target_file)) {
            } else {
            
            }
        }
    }

?>


