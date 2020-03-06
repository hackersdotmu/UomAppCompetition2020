<?php
class Org{
 
    private $database;
    private $conn;
 
    public $orgId;
    public $name;
    public $description;
    public $phone;
    public $address;
    public $lattitude;
    public $longitude;
    public $verified;
    public $created_on;
 
    public function __construct($database,$common){
        $this->database= $database;
        $this->connection=$database;
        //$this->conn = $database->getConnection();
        $this->common=$common;
    }
}