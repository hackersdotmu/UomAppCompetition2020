<?php
class Slot{
 
    private $database;
    private $conn;
 
    public $slotId;
    public $orgId;
    public $recur;
    public $qty_req;
    public $qty_rec;
    public $date;
    public $start;
    public $end;
    public $url;
    public $status;
    public $created_on;
 
    public function __construct($database,$common){
        $this->database= $database;
        $this->connection=$database;
        //$this->conn = $database->getConnection();
        $this->common=$common;
    }
}