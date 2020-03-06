<?php
class Requirements{
 
    private $database;
    private $conn;
 
    public $reqId;
    public $slotId;
    public $name;
 
    public function __construct($database,$common){
        $this->database= $database;
        $this->connection=$database;
        //$this->conn = $database->getConnection();
        $this->common=$common;
    }
}