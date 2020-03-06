<?php
    include('../config/common.php');
    include('../config/database.php');
    include('../controllers/slots.php');

    $common=new common();
    $database=new Database();
    $slots=new slots($database,$common);

    $params = json_decode(file_get_contents('php://input'), true);

    if (isset($params['action'])){
        $action=$params['action'];
    
        switch($action){
            case 'add_slot':
                $slots->add_slot($params);
                break;
            case 'get_slot':
                $slots->fetch_slot($params);
                break;
            case 'get_all_slots':
                $slots->fetch_all_slots($params);
                break;
            case 'get_donated_slots':
                $slots->fetch_all_slots_donated($params);
                break;
            case 'get_slots_by_org':
                $slots->fetch_slots_by_org($params);
                break;
            case 'update_qty_rec':
                $slots->update_qty_rec($params);
                break;
            case 'delete_slot':
                $slots->delete_slot($params);
                break;
            case 'toggle_image_slot':
                $slots->toggle_image_slot($params);
                break;
           
            default:
                $common->respond();
        }
    }else{
        $common->respond();
    }
?>
