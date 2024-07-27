package com.emms.schema;

/**
 * Created by Administrator on 2016/7/14.
 */
public class Task {
    public static final String TASK_ID = "Task_ID";
    public static final String Receiver = "Receiver";//kingzhang add 20210415
    public static final String TASK_TYPE = "TaskType";
    public static final String OPERATOR_ID = "Operator_ID";
    public static final String TEAM_ID = "Team_ID";
    public static final String TASK_CLASS = "TaskClass";
    public static final String TASK_SUBCLASS = "TaskSubClass";
    public static final String APPLICANT = "Applicant";//申请人
    public static final String APPLICANT_TIME = "ApplicantTime";
    public static final String START_TIME = "StartTime";
    public static final String FINISH_TIME = "FinishTime";
    public static final String ORGANISE_NAME = "TaskApplicantOrg";
    public static final String TASK_DESCRIPTION = "TaskDescr";
    public static final String TASK_STATUS = "DataName";
    public static final String REPAIR_TASK = "T01";
    public static final String MAINTAIN_TASK = "T02";
    public static final String MOVE_CAR_TASK = "T05";
    public static final String TRANSFER_MODEL_TASK = "T06";
    public static final String OTHER_TASK = "T04";
    public static final String ROUTING_INSPECTION = "T0202";
    public static final String UPKEEP = "T0201";
    public static final String GROUP_ARRANGEMENT = "T07";
    public static final String logicType = "logicType";
    public static final String ModuleType = "ModuleType";
    public static final String TASK_OPERATOR_STATUS = "TaskOperatorStatus";

    //定义转款任务类型
    public static final String TRANSFER_TASK = "T08";
    public static final String TRANSFER_CHECK_IN = "style_change_check_in";
    public static final String TRANSFER_REJECT = "style_change_";

    //定义备件页面活动类型 2021-12-07 Abrahamguo 添加备件查询和备件挑选页面类型
    public static final String SPAREPART_CHOOSE = "spare_part_choose"; //备件挑选(创建申请单)
    public static final String SPAREPART_EQUIPMENT_CHOOSE = "spare_part_equipment_choose"; //备件挑选(机器)
    public static final String SPAREPART_REQUEST = "spart_part_request";//备件申请
    public static final String SPAREPART_RETURN = "spart_part_return";//备件回退
    public static final String SPAREPART_CONFIRM = "spart_part_confirm";//备件确认
    public static final String SPAREPART_DETAILS = "spart_part_details";//备件申请单详情
    public static final String SPAREPART_SURPLUS = "spart_part_surplus";//备件剩余
    public static final String SPAREPART_TASK_USED = "spart_part_task_used";//备件已使用(任务)
    public static final String SPAREPART_EQUIPMENT_USED = "spart_part_equipment_used";//备件已使用(机器)
    public static final String SPAREPART_CHECK_MESSAGE = "spare_part_check_message";//申请单详情中check_message不为空

    //设备借还 2022-3-7 kingzhang 新增设备借还页面入口
    public static final String Equipment_Borrow_Return = "T09,T10";//设备借还
    //定义设备借还任务类型 kingzhang for srf 2022-0106
    public static final String Lend_TASK = "T09";
    public static final String Borrow_TASK = "T10";
    public static final String Move_TO = "TargetTeam";
    public static final String Move_From = "MoveFromName";
    public static final String Verify_TASK = "C1";
}
