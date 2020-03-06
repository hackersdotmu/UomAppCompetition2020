<?php
class Database{
    private $host = "130.61.106.124";
    private $db_name = "pleinvent";
    private $username = "appcup";
    private $password = "tiboulo90";
    public $conn;


    public function __construct(){
       $this->connect();
    }

    public function connect(){
        $this->conn = null;
        try{
            $this->conn = new PDO("mysql:host=" . $this->host . ";dbname=" . $this->db_name, $this->username, $this->password);
            $this->conn->exec("set names utf8");

        }catch(PDOException $exception){
            echo "Connection error: " . $exception->getMessage();
        }
 
        return $this->conn;
    }

    public function query($query_string){
        $query = $this->conn->prepare($query_string);
        $query->execute();
        return $query;
    }

}