<?php
    include('../config/common.php');
    include('../config/database.php');
    include('../controllers/bookings.php');

    $common=new common();
    $database=new Database();
    $bookings=new bookings($database,$common);

    $params = json_decode(file_get_contents('php://input'), true);

    if (isset($params['action'])){
        $action=$params['action'];
    
        switch($action){
            case 'book':
                $bookings->commit($params);
                break;
            case 'confirm';
                $bookings->confirm($params);
                break;
            case 'get_booking':
                $bookings->fetch_booking($params);
                 break;
            case 'get_donor_bookings':
                $bookings->fetch_donor_bookings($params);
                break;
           
            default:
                $common->respond();
        }
    }else{
        $common->respond();
    }
?>
