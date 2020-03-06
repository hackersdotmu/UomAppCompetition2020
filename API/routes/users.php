<?php
    include('../controllers/users.php');
    include('../config/common.php');
    include('../config/database.php');

    $common=new common();
    $database=new Database();
    $users=new users($database,$common);
   

    $params = json_decode(file_get_contents('php://input'), true);
    //print_r($params);
    
    if (isset($params['action'])){
        $action=$params['action'];
        switch($action){
            case 'login':
                $users->login($params);
                break;
            case 'register':
                $users->register($params);
                break;
            case 'get_user':
                $users->fetch_user($params);
                break;
            case 'get_all_users':
                $users->fetch_all_users($params);
                break;
            case 'update_user':
                $users->update_user($params);
                break;
            default:
                $common->respond();
        }
    }else{
        $common->respond();
    }
?>