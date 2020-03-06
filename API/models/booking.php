<?php
class Booking{
 
    private $database;
    private $conn;
 
    public $bookingId;
    public $slotId;
    public $mealId;
    public $received;
    public $created_on;
 
    public function __construct($database,$common){
        $this->database= $database;
        $this->connection=$database;
        //$this->conn = $database->getConnection();
        $this->common=$common;
    }

    


}