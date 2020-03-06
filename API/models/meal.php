<?php
class Meal{
 
    private $database;
    private $conn;
 
    public $mealId;
    public $qty;
    public $date_prepared;
    public $f_energy;
    public $f_health;
    public $f_growth;
    public $created_on;
 
    public function __construct($database,$common){
        $this->database= $database;
        $this->connection=$database;
        //$this->conn = $database->getConnection();
        $this->common=$common;
    }
}