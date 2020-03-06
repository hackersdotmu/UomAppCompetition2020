<?php
    include('../models/booking.php');
    include('../models/meal.php');
    include('../models/requirements.php');

    Class bookings{
        private $common;
        private $database;
        private $conn;

        public function __construct($database,$common){
            $this->database= $database;
            //$this->conn = $database->getConnection();
            $this->conn = $database;
            $this->common= $common;
            $this->booking= new Booking($database,$common);
            $this->meals= new Meal($database,$common);
            $this->requirements= new Requirements($database,$common);
        }

        public function commit($params){
            //$bookingId=$params['bookingId'];
            $bookingId=$this->get_next_booking_id();
            $mealId=$this->get_next_meal_id();
            $slotId=$params['slotId'];
            $donorId=$params['donorId'];
            $qty=$params['qty'];
            $date_prepared=$params['date_prepared'];
            // $f_energy=$params['f_energy'];
            // $f_health=$params['f_health'];
            // $f_growth=$params['f_growth'];
               
            if(isset($bookingId) && isset($mealId) && isset($slotId) && isset($donorId)){
                $this->insert_booking($bookingId,$slotId,$donorId,$mealId);
               // $this->insert_meal($mealId,$qty,$date_prepared,$f_energy,$f_health,$f_growth);
                $this->insert_meal($mealId,$qty,$date_prepared);

                $response=array(
                    "success" => true,
                    "booking" => $this->get_booking($bookingId)
                );               
                $this->common->respond(json_encode($response));
                //print_r(json_encode($this->get_slot($slotId)));
            }else{
                $this->common->respond();
            }
        }

        public function fetch_booking($params){
            $bookingId=$params['bookingId'];
                    
            if(isset($bookingId)){
                $response=array(
                    "success" => true,
                    "booking" => $this->get_booking($bookingId)
                );               
                $this->common->respond(json_encode($response));
                //print_r(json_encode($this->get_slot($slotId)));
            }else{
                $this->common->respond();
            }
        }

        public function fetch_donor_bookings($params){
            $donorId=$params['donorId'];
                    
            if(isset($donorId)){
                $response=array(
                    "success" => true,
                    "booking" => $this->get_donor_bookings($donorId)
                );               
                $this->common->respond(json_encode($response));
                //print_r(json_encode($this->get_slot($slotId)));
            }else{
                $this->common->respond();
            }
        }

        

        

        public function confirm($params){
            $bookingId=$params['bookingId'];
            
            if(isset($bookingId)){
                $response= $this->database->query("UPDATE pleinvent.booking SET received='1' WHERE bookingId='{$bookingId}';");
    
                $num = $response->rowCount();
               
                if ($num>0){
                    $response=array(
                        "success" => true,
                        "user" => $this->get_booking($bookingId)
                    );
                    
                    $this->common->respond(json_encode($response));
                }else{
                    $this->common->respond();
                }

            }else{
                $this->common->respond();
            }
        }

        public function insert_booking($bookingId,$slotId,$donorId,$mealId){

            $response= $this->database->query("INSERT INTO pleinvent.booking VALUES('{$bookingId}','{$slotId}','{$donorId}','{$mealId}',false,NOW());");
        //    print_r("INSERT INTO pleinvent.slots VALUES('{$slotId}','{$orgId}','{$recur}','{$qty_req}','{$qty_rec}', '{$date}','{$start}','{$end}', true, false,NOW());");
            $num = $response->rowCount();        
            if ($num>0){
                return true;
            }else{
                return false;
            }
        }

        //public function insert_meal($mealId,$qty,$date_prepared,$f_energy,$f_health,$f_growth){
        public function insert_meal($mealId,$qty,$date_prepared){
            // if($f_energy==false){
            //     $f_energy=0;
            // }else if($f_energy==true){
            //     $f_energy=1;
            // }
            // if($f_health==false){
            //     $f_health=0;
            // }else if($f_health==true){
            //     $f_health=1;
            // }
            // if($f_growth==false){
            //     $f_growth=0;
            // }else if($f_growth==true){
            //     $f_growth=1;
            // }

            // $response= $this->database->query("INSERT INTO pleinvent.meal VALUES('{$mealId}','{$qty}','{$date_prepared}','{$f_energy}','{$f_health}','{$f_growth}',NOW());");
            $response= $this->database->query("INSERT INTO pleinvent.meal VALUES('{$mealId}','{$qty}','{$date_prepared}',NOW());");
            //print_r("INSERT INTO pleinvent.meal VALUES('{$mealId}','{$qty}','{$date_prepared}','{$f_energy}','{$f_health}','{$f_growth}', NOW());");
            $num = $response->rowCount();        
            if ($num>0){
                return true;
            }else{
                return false;
            }
        }

        public function get_booking($bookingId){
            $response= $this->database->query("SELECT * FROM booking b, meal m WHERE bookingId='{$bookingId}' and b.mealId=m.mealId;");
           // print_r("SELECT * FROM booking b, meal m WHERE bookingId='{$bookingId}' and b.mealId=m.mealId;");
    
            $num = $response->rowCount(); 
            if ($num>0){
                $record=$response->fetch();
                                
                $booking=array(
                    "bookingId" => $record['bookingId'],
                    "slotId" => $record['slotId'],
                    "donorId" => $record['donorId'],
                    "received" => $record['received'],
                    "mealId" => $record['mealId'],
                    "qty" => $record['qty'],
                    "date_prepared" => $record['date_prepared'],
                    ///"f_energy" => $record['f_energy'],
                   /// "f_health" => $record['f_health'],
                   // "f_growth" => $record["f_growth"],
                    "created_on" => $record['created_on'], 
                );
                return $booking;
            }else{
                return null;
            }
        }

        public function get_donor_bookings($donorId){
            $response= $this->database->query("SELECT * FROM booking b, meal m WHERE donorId='{$donorId}' and b.mealId=m.mealId;");
           // print_r("SELECT * FROM booking b, meal m WHERE bookingId='{$bookingId}' and b.mealId=m.mealId;");
    
           $response = $response -> fetchAll();

           $results=array();
            foreach( $response as $record ) {
                $booking=$this->get_booking($record['bookingId']);
                array_push($results, $booking);
            }
            return $results;
            
        }



        public function get_next_booking_id(){
            $response= $this->database->query("SELECT * FROM booking");
            $num = $response->rowCount(); 
            if ($num>0){
                return $num;
            }else{
                return 0;
            }
        }

        public function get_next_meal_id(){
            $response= $this->database->query("SELECT * FROM meal");
            $num = $response->rowCount(); 
            if ($num>0){
                return $num;
            }else{
                return 0;
            }
        }

        







    }


?>