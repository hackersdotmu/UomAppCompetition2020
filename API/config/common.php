<?php
    Class common{
        public function respond($message="error"){
            $statusCode=1;
            if ($message=="error"){
                $statusCode=0;
            }
            $response=array(
                "statuscode"=>$statusCode,
                "response"=>$message
            );
            
            http_response_code(200);
            echo json_encode($response);
        }
        
        public function host(){
            return("http://localhost/api");
        }

        public function slots_images_folder(){
            return('http://unchain.ml:85/newapi/datastore/slots/');
        }

    }
  
?>