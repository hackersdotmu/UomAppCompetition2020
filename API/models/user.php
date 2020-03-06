<?php
class User{
 
    private $database;
    private $conn;
 
    public $userId;
    public $email;
    public $password;
    public $targetId;
    public $created_on;
 
    public function __construct($database,$common){
        $this->database= $database;
        $this->connection=$database;
        //$this->conn = $database->getConnection();
        $this->common=$common;
    }
}