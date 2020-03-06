<?php
    include('../models/meal.php');
    include('../models/requirements.php');
    include('../models/slot.php');

    Class slots{
        private $common;
        private $database;
        private $conn;

        public function __construct($database,$common){
            $this->database= $database;
            //$this->conn = $database->getConnection();
            $this->conn = $database;
            $this->common= $common;
            $this->slot= new Slot($database,$common);
            $this->req= new Requirements($database,$common);

        }

        public function add_slot($params){
            $orgId=$params['orgId'];
            $recur=$params['recur'];
            $qty_req=$params['qty_req']; 
            $qty_rec=$params['qty_rec'];
            $date=$params['date']; 
            $start=$params['start']; 
            $end=$params['end'];
            $reqs=$params['reqs'];

            $slotId=$this->get_next_slot_id();

            if(isset($slotId) && isset($orgId) && isset($recur) && isset($qty_req) && isset($qty_rec) && isset($date) && isset($start) && isset($end) && isset($reqs)){
   
                if ($response= $this->insert_slot($slotId,$orgId,$recur,$qty_req,$qty_rec, $date,$start,$end,$reqs)==true){

                    $response=array(
                        "success" => true,
                        "user" => $this->get_slot($slotId)
                    );
                   
                    $this->common->respond(json_encode($response));
                }else{
                    $this->common->respond();
                }
            }else{
                $this->common->respond();
            }
        }

        public function update_qty_rec($params){
            $slotId=$params['slotId'];
            $qty_rec=$params['qty_rec'];
            
            if(isset($slotId) && isset($qty_rec)){
                $response= $this->database->query("UPDATE pleinvent.slots SET qty_rec='{$qty_rec}' WHERE slotId='{$slotId}';");
    
                $num = $response->rowCount();
               
                if ($num>0){
                    $response=array(
                        "success" => true,
                        "user" => $this->get_slot($slotId)
                    );
                    
                    $this->common->respond(json_encode($response));
                }else{
                    $this->common->respond();
                }

            }else{
                $this->common->respond();
            }
        }

        public function delete_slot($params){
            $slotId=$params['slotId'];
            
            if(isset($slotId)){
                $response= $this->database->query("UPDATE pleinvent.slots SET status=false WHERE slotId='{$slotId}';");
    
                $num = $response->rowCount();
               
                if ($num>0){
                    $response=array(
                        "success" => true,
                        "user" => $this->get_slot($slotId)
                    );
                    
                    $this->common->respond(json_encode($response));
                }else{
                    $this->common->respond();
                }

            }else{
                $this->common->respond();
            }
        }

        public function toggle_image_slot($params){
            $slotId=$params['slotId'];
            
            if(isset($slotId)){
                $response=$this->database->query("UPDATE pleinvent.slots SET url=true WHERE slotId='{$slotId}';");
    
                $num = $response->rowCount();
               
                if ($num>0){
                    $response=array(
                        "success" => true,
                        "user" => $this->get_slot($slotId)
                    );
                    
                    $this->common->respond(json_encode($response));
                }else{
                    $this->common->respond();
                }

            }else{
                $this->common->respond();
            }
        }

        public function fetch_slot($params){
            $slotId=$params['slotId'];
                    
            if(isset($slotId)){
                $response=array(
                    "success" => true,
                    "slot" => $this->get_slot($slotId)
                );               
                $this->common->respond(json_encode($response));
                //print_r(json_encode($this->get_slot($slotId)));
            }else{
                $this->common->respond();
            }
        }

        public function fetch_slots_by_org($params){
            if (isset($params['num'])){
                $num=$params['num'];
            }else{
                $num=20;
            }
            $orgId=$params['orgId'];
            $slots= $this->get_slots_by_org($orgId, $num);
            if($slots!=false){
                $response=array(
                    "success" => true,
                    "slots" =>$slots
                );
                
                $this->common->respond(json_encode($slots));
            }else{
                $this->common->respond();
           }
        }



        public function get_slot($slotId){
            $response= $this->database->query("SELECT * FROM slots s, organisation o WHERE slotId='{$slotId}' and s.orgId=o.orgId");
            //print_r("SELECT * FROM slots s, organisation o WHERE slotId='{$slotId}' and s.orgId=o.orgId;");
    
            $num = $response->rowCount(); 
            if ($num>0){
                $record=$response->fetch();
                $url=$record["url"];
                if($url==true){
                    $url=($this->common->slots_images_folder().strVal($slotId).".png");
                }
                $reqs=$this->get_reqs($slotId);
                
                $slot=array(
                    "slotId" => $record['slotId'],
                    "orgId" => $record['orgId'],
                    "orgName" => $record['name'],
                    "orgAddress" => $record['address'],
                    "orgLatitude" => $record['latitude'],
                    "orglongitude" => $record['longitude'],
                    "recur" => $record['recur'],
                    "qty_req" => $record['qty_req'],
                    "qty_rec" => $record['qty_rec'],
                    "date" => $record["date"],
                    "start" => $record["start"],
                    "end" => $record["end"],
                    "reqs" => $reqs,
                    "url" => $url,
                    "priorities" => $this->generate_priority($record['created_on'],$record["date"],$record['qty_req'],$record['qty_rec']),
                    "status" => $record["status"],
                    "created_on" => $record['created_on'], 
                );
                return $slot;
            }else{
                return null;
            }
        }

        public function get_slots_by_org($orgId,$num){
    
            $response= $this->database->query("SELECT * FROM pleinvent.slots WHERE  orgId='{$orgId}';");
            $response = $response -> fetchAll();

            $results=array();
            $i=0;
            foreach( $response as $record ) {
                $slot=$this->get_slot($record['slotId']);
                if($i<$num){
                    array_push($results, $slot);
                }
                $i++;
            }
           return $results;
        }
       
        public function fetch_all_slots($params){
            if (isset($params['num'])){
                $num=$params['num'];
            }else{
                $num=20;
            }
            $slots= $this->get_slots($num);
            if($slots!=false){
                $response=array(
                    "success" => true,
                    "slots" =>$slots
                );
                
                $this->common->respond(json_encode($slots));
            }else{
                $this->common->respond();
           }
        }

        public function fetch_all_slots_donated($params){
            $donorId=$params['donorId'];
            $slots= $this->get_slots_donated($donorId);
            if($slots!=false){
                $response=array(
                    "success" => true,
                    "slots" =>$slots
                );
                
                $this->common->respond(json_encode($slots));
            }else{
                $this->common->respond();
           }
        }

        public function get_slots($num){
            
            $response= $this->database->query("SELECT * FROM pleinvent.slots");
            $response = $response -> fetchAll();

            $results=array();
            $i=0;
            foreach( $response as $record ) {
                $user=$this->get_slot($record['slotId']);
                if($i<$num){
                    array_push($results, $user);
                }
                $i++;
            }
           return $results;
           
        }

        public function get_slots_donated($donorId){
            
            $response= $this->database->query("SELECT * FROM slots s, booking b WHERE s.slotId=b.slotId and b.donorId='{$donorId}'");
            $response = $response -> fetchAll();

            $results=array();
            foreach( $response as $record ) {
                $user=$this->get_slot($record['slotId']);
                array_push($results, $user);
            }
            
           return $results;
        }
       
 


        public function insert_slot($slotId,$orgId,$recur,$qty_req,$qty_rec, $date,$start,$end,$reqs){
            
            if($reqs!=[]){
                $this->insert_reqs($slotId,$reqs);
            }
                   
            if($recur==false){
                $recur=0;
            }else if($recur==true){
                $recur=1;
            }
            $response= $this->database->query("INSERT INTO pleinvent.slots VALUES('{$slotId}','{$orgId}','{$recur}','{$qty_req}','{$qty_rec}', '{$date}','{$start}','{$end}', true, false,NOW());");
        //    print_r("INSERT INTO pleinvent.slots VALUES('{$slotId}','{$orgId}','{$recur}','{$qty_req}','{$qty_rec}', '{$date}','{$start}','{$end}', true, false,NOW());");
            $num = $response->rowCount();        
            if ($num>0){
                return true;
            }else{
                return false;
            }
        }

        public function insert_reqs($slotId,$reqs){
            foreach( $reqs as $req ) {
                $reqId=$this->get_next_req_id();
                $this->database->query("INSERT INTO pleinvent.requirements VALUES('{$reqId}','{$slotId}','{$req}');");
                //print_r("INSERT INTO pleinvent.requirements VALUES('{$reqId}','{$slotId}','{$req}');");
            }

        }

        public function get_reqs($slotId){
            $response=$this->database->query("SELECT * FROM requirements WHERE slotId='{$slotId}';");
            $num = $response->rowCount();          
            if ($num>0){
                $response = $response -> fetchAll();
                $results=array();
            
                foreach( $response as $record ) {
                    array_push($results, $record['name']);
                }
                return $results;
                //$this->common->respond(json_encode($response));
            }else{
                //$this->common->respond();
                return false;
            }
        }

        public function generate_priority($start_date,$end_date,$qty_req,$qty_rec){  /// adddate comparison
            //print_r($start_date."  ".$end_date);
            $start = new DateTime($start_date);
            $end= new DateTime($end_date);
            $now=new DateTime();
            $duration=($end->diff($start))->d;
            $remaining=($end->diff($now))->d;

            $req_time_factor=(1-($remaining/$duration))/2;
            $req_qty_factor=(($qty_req-$qty_rec)/$qty_req)/2;

            $priority=$req_qty_factor+$req_time_factor;
           
            //print($priority);
            return($priority);
        }


        public function get_next_slot_id(){
            $response= $this->database->query("SELECT * FROM slots");
            $num = $response->rowCount(); 
            if ($num>0){
                return $num;
            }else{
                return 0;
            }
        }

        public function get_next_req_id(){
            $response= $this->database->query("SELECT * FROM requirements");
            $num = $response->rowCount(); 
            if ($num>0){
                return $num;
            }else{
                return 0;
            }
        }

    }


?>