<?php
    include('../models/user.php');
    include('../models/org.php');
    include('../models/donor.php');

    Class users{
        private $common;
        private $database;
        private $conn;
        private $auth;
        private $user;

        public function __construct($database,$common){
            $this->database= $database;
            $this->connection= $database;
            //$this->conn = $database->getConnection();
            $this->common= $common;
            //$this->auth= new Auth($database,$common);
            $this->user= new User($database,$common);
            $this->donor= new Donor($database,$common);
            $this->org= new Org($database,$common);
        }
        

        public function login($params){
            //if(isset($_POST['em']) && isset($_POST['hash'])){
            $email=$params['email'];
            $password=$params['password'];
            if(isset($email) && isset($password)){
                
                $response= $this->database->query("SELECT userId, email,password FROM users WHERE email='{$email}'");
                $num = $response->rowCount(); 
                if ($num>0){
                    $record=$response->fetch();
                    $hash=$record['password'];
                    $userId=$record['userId'];
                    if (password_verify($password, $hash)) {
                        $response=array(
                            "success" => true,
                            "user" => $this->get_user_by_id($userId)
                        );
                        $this->common->respond(json_encode($response));
                    }else{
                        $this->common->respond();
                    }
                }else{
                    $this->common->respond();
                }

            }else{
                $this->common->respond();
           }
        }

        public function register($params){
            $email=$params['email'];
            $password=$params['password'];
            $type=$params['type'];  //donor or organisation
            $userId=$this->get_next_user_id();
            
            if(isset($email) && isset($password) && isset($type)){
                if ($response= $this->insert_user($userId,$email,$password,$type)==true){
                    $response=array(
                        "success" => true,
                        "user" => $this->get_user_by_id($userId)
                    );
                    //print_r($response);
                    
                    $this->common->respond(json_encode($response));
                }else{
                    $this->common->respond();
                }
            }else{
                $this->common->respond();
            }
        }



        public function fetch_user($params){
            $userId=$params['userId'];
                    
            if(isset($userId)){
                $response=array(
                    "success" => true,
                    "user" => $this->get_user_by_id($userId)
                );               
                $this->common->respond(json_encode($response));
               
            }else{
                $this->common->respond();
            }
        }

        public function fetch_all_users($params){
            if (isset($params['num'])){
                $num=$params['num'];
            }else{
                $num=20;
            }
            $users= $this->get_users($num);
            if($users!=false){
                $response=array(
                    "success" => true,
                    "users" =>$users
                );
                
                $this->common->respond(json_encode($users));
            }else{
                $this->common->respond();
           }
        }

        public function update_user($params){
            $userId=$params['userId'];
            $user=$this->get_user_by_id($userId);
            $type=$user['type'];

            if(isset($params['name'])){
                $name=$params['name'];
            }else{
                $name=$user['name'];
            }
            if(isset($params['phone'])){
                $phone=$params['phone'];
            }else{
                $phone=$user['phone'];
            }
            if(isset($params['address'])){
                $address=$params['address'];
            }else{
                $address=$user['address'];
            }
            if(isset($params['latitude'])){
                $latitude=$params['latitude'];
            }else{
                $latitude=$user['latitude'];
            }
            if(isset($params['longitude'])){
                $longitude=$params['longitude'];
            }else{
                $longitude=$user['longitude'];
            }
           
           
            if($type=="donor"){
                $donorId=$user["donorId"];
               
                if(isset($params['points'])){
                    $points=$params['points'];
                }else{
                    $points=$user['points'];
                }

                // if(isset($donorId) && isset($name) && isset($phone) && isset($address) && isset($latitude) && isset($longitude) && isset($points)){
                    if ($this->update_donor($donorId,$name,$phone,$address,$latitude,$longitude,$points)==true){
                        $user=$this->get_user_by_id($userId);
                        if($user!=null){
                            $response=array(
                                "success" => true,
                                "user" => $this->get_user_by_id($userId)
                            );
                            $this->common->respond(json_encode($response));
                        }
                    }else{
                        $this->common->respond();
                    }
                // }
            }else if($type=="organisation"){
                $orgId=$user["orgId"];
                if(isset($params['description'])){
                    $description=$params['description'];
                }else{
                    $description=$user['description'];
                }
                if(isset($params['verified'])){
                    $verified=$params['verified'];
                }else{
                    $verified=$user['verified'];
                }

                // if(isset($orgId) && isset($name) && isset($description) && isset($address) && isset($latitude) && isset($longitude) && isset($verified)){
                 
                    if ($this->update_org($orgId,$name,$description,$phone,$address,$latitude,$longitude,$verified)==true){
                        $user=$this->get_user_by_id($userId);
                        if($user!=null){
                            $response=array(
                                "success" => true,
                                "user" => $this->get_user_by_id($userId)
                            );
                            print_r('ok');
                            $this->common->respond(json_encode($response));
                        }
                    }else{
                        $this->common->respond();
                    }
                //}
            }else{
                $this->common->respond();
            }
        }





       
        //Self call functions

        public function get_user_by_id($userId){
            $response= $this->database->query("SELECT userId, email, type, targetId, created_on FROM users WHERE userId='{$userId}'");
    
            $num = $response->rowCount(); 
            if ($num>0){
                $record=$response->fetch();
                $type=$record['type'];
                $targetId=$record['targetId'];
                

                if($type=="donor"){
                    $response= $this->database->query("SELECT donorId, name, phone, address, latitude, longitude, points, created_on FROM donor WHERE donorId='{$targetId}'");
                    $donor=$response->fetch();
                    
                    $user=array(
                        "userId" => $record['userId'],
                        "email" => $record['email'],
                        "type" => $record['type'],
                        "targetId" => $record['targetId'],
                        "donorId" => $donor['donorId'],
                        "name" => $donor["name"],
                        "phone" => $donor["phone"],
                        "address" => $donor["address"],
                        "latitude" => $donor["latitude"],
                        "longitude" => $donor["longitude"],
                        "points" => $donor["points"],
                        "created_on" => $record['created_on'],
                    );

                }else if($type=="organisation"){

                    $response= $this->database->query("SELECT orgId, name, description, phone, address, latitude, longitude, verified, created_on FROM organisation WHERE orgId='{$targetId}'");
                    $org=$response->fetch();
                    $user=array(
                        "userId" => $record['userId'],
                        "email" => $record['email'],
                        "type" => $record['type'],
                        "targetId" => $record['targetId'],
                        "orgId" => $org['orgId'],
                        "name" => $org["name"],
                        "description" => $org["description"],
                        "phone" => $org["phone"],
                        "address" => $org["address"],
                        "latitude" => $org["latitude"],
                        "longitude" => $org["longitude"],
                        "verified" => $org["verified"],
                        "created_on" => $record['created_on'],
                    );
                }else{
                    return null;
                }
               
                return $user;
            }else{
                return null;
            }
        }

        public function get_users($num){
            
            $response= $this->database->query("SELECT * FROM pleinvent.users");
            $response = $response -> fetchAll();

            $results=array();
            $i=0;
            foreach( $response as $record ) {
                $user=$this->get_user_by_id($record['userId']);
                if($i<$num){
                    array_push($results, $user);
                }
                $i++;
            }
           return $results;
           
        }


        
        public function insert_user($userId,$email,$password,$type){
            $hash=password_hash($password, PASSWORD_DEFAULT);

            if($type=="donor"){
                $targetId=$this->init_donor();
            }else if($type=="organisation"){
                $targetId=$this->init_org();
            }else{
                return false;
            }

            $response= $this->database->query("INSERT INTO pleinvent.users VALUES('{$userId}','{$email}','{$hash}','{$type}','{$targetId}', NOW());");
           
            $num = $response->rowCount();        
            if ($num>0){
                return true;
            }else{
                return false;
            }
        }
        // $response= $this->database->query("UPDATE pleinvent.users SET name=".$name.", phone=".$phone.", address=".$address.", latitude=".$latitude.", longitude=".$longitude." , points=".$points." WHERE donorId='{$donorId}';");
        // print_r("UPDATE pleinvent.users SET name=".$name.", phone=".$phone.", address=".$address.", latitude=".$latitude.", longitude=".$longitude." , points=".$points." WHERE donorId='{$donorId}';");
        // // if(!isset($phone)){

        public function update_donor($donorId,$name,$phone,$address,$latitude,$longitude,$points){
            
            if(!isset($name)){
                $name="NULL";
            }else{
                $name="'".$name."'";
            }
            if(!isset($phone)){
                $phone="NULL";
            }else{
                $phone="'".$phone."'";
            }
            if(!isset($address)){
                $address="NULL";
            }else{
                $address="'".$address."'";
            }
            if(!isset($latitude)){
                $latitude="NULL";
            }else{
                $latitude="'".$latitude."'";
            }
            if(!isset($longitude)){
                $longitude="NULL";
            }else{
                $longitude="'".$longitude."'";
            }
            if(!isset($points)){
                $points="NULL";
            }else{
                $points="'".$points."'";
            }   

            $response= $this->database->query("UPDATE pleinvent.donor SET name=$name, phone=$phone, address=$address, latitude=$latitude, longitude=$longitude, points=$points WHERE donorId='{$donorId}';");

    
            $num = $response->rowCount();
           
            if ($num>0){
                return true;
            }else{
                return false;
            }
        }

        public function update_org($orgId,$name,$description,$phone,$address,$latitude,$longitude,$verified){
            if(!isset($name)){
                $name="NULL";
            }else{
                $name=addslashes($name);
                $name="'".$name."'";
                
            }
            if(!isset($description)){
                $description="NULL";
            }else{
                $description=addslashes($description);
                $description="'".$description."'";
               
            }
            if(!isset($phone)){
                $phone="NULL";
            }else{
                $phone="'".$phone."'";
            }
            if(!isset($address)){
                $address="NULL";
            }else{
                $address="'".$address."'";
            }
            if(!isset($latitude)){
                $latitude="NULL";
            }else{
                $latitude="'".$latitude."'";
            }
            if(!isset($longitude)){
                $longitude="NULL";
            }else{
                $longitude="'".$longitude."'";
            }


            if(!isset($verified)){
                $verified="NULL";
            }else{
                 if($verified==false){
                    $verified="false";
                }else if($verified==true){
                    $verified="true";
                }
            }
            
            $response= $this->database->query("UPDATE pleinvent.organisation SET name=$name, description=$description, phone=$phone, address=$address, latitude=$latitude, longitude=$longitude, verified=$verified WHERE orgId=$orgId;");

            //print_r("UPDATE pleinvent.organisation SET name=$name, description=$description, phone=$phone, address=$address, latitude=$latitude, longitude=$longitude, verified=$verified WHERE orgId=$orgId;");
            
            $num = $response->rowCount();
           
            if ($num>0){
                return true;
            }else{
                return false;
            }
        }
        
        public function init_donor(){
            $donorId=$this->get_next_donor_id();
            $response= $this->database->query("INSERT INTO pleinvent.donor VALUES('{$donorId}',null,null,null,null,null,null, NOW());");
            return $donorId;
        }
        public function init_org(){
            $orgId=$this->get_next_org_id();
            $response= $this->database->query("INSERT INTO pleinvent.organisation VALUES('{$orgId}',null,null,null,null,null,null,null, NOW());");
            return $orgId;
        }


        public function get_next_user_id(){
            $response= $this->database->query("SELECT * FROM users");
            $num = $response->rowCount(); 
            if ($num>0){
                return $num;
            }else{
                return 0;
            }
        }

        public function get_next_org_id(){
            $response= $this->database->query("SELECT * FROM organisation");
            $num = $response->rowCount(); 
            if ($num>0){
                return $num;
            }else{
                return 0;
            }
        }

        public function get_next_donor_id(){
            $response= $this->database->query("SELECT * FROM donor");
            $num = $response->rowCount(); 
            if ($num>0){
                return $num;
            }else{
                return 0;
            }
        }

    }

    


?>