<?php
class Donor{
 
    private $database;
    private $conn;
 
    public $donorId;
    public $name;
    public $phone;
    public $address;
    public $latitude;
    public $longitude;
    public $points;
    public $created_on;
 
    public function __construct($database,$common){
        $this->database= $database;
        $this->connection=$database;
        //$this->conn = $database->getConnection();
        $this->common=$common;
    }
}