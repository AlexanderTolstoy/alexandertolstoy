# -*- coding: utf-8 -*-
# @Time : 2020/5/8 10:39 上午
# @Author : Elazer
# @Version :
# @Function :
# @Warning : YOU CAN(NOT) REDO.

from impala.dbapi import connect
import pandas as pd
import numpy as np
from sqlalchemy import create_engine
import psycopg2
import datetime as dt
import arrow
import threading as th
from sqlalchemy.types import VARCHAR, Float, Integer
import json


# import random


# 取数:
# 员工校区和部门
def get_emp_inf(from_con):
    sql = """
        SELECT 
        dept_mem.employee_id as emp_id,
        dept_mem.dept_id as dept_id,
        scl.id as school_id
         FROM rods.rods_jlbapp_jg_dept_member_f_kudu as dept_mem
        left join 
        rods.rods_jlbapp_jg_dept_f_kudu as dept_scl
        on dept_mem.dept_id = dept_scl.dept_id
        left join
        rods.rods_eip_ecp_school_f_kudu as scl
        on dept_scl.jg_department_id=scl.uid
        WHERE
        dept_mem.is_deleted<>1
        and dept_scl.is_deleted<>1
    """
    data = pd.read_sql(sql=sql, con=from_con)
    return data


# 外呼通话记录 + case_id
def get_call_data(from_con, start_date):
    sql = """
        SELECT 
        call.*,
        case_inf.id as case_id 

        FROM
        (
        SELECT 
        tel_dt.org_id,
        tel_dt.emp_id,
        tel_dt.called_number,
        tel_dt.start_time,
        tel_rc.recording_duration as duration,
        tel_dt.contact_disposition
        FROM 
        rods.rods_jlbapp_telephone_call_detail_f_kudu as tel_dt
        LEFT JOIN 
        rods.rods_jlbapp_telephone_call_detail_recording_f_kudu as tel_rc
        ON tel_dt.id = tel_rc.detail_id
        WHERE
        tel_dt.contact_type = 'Outbound'
        AND
        tel_dt.start_time >= 'start_time'

        UNION ALL 

        SELECT
        org_id,
        employ_id as emp_id,
        called_number, 
        start_time, 
        duration,
        contact_disposition
        FROM 
        rods.rods_jlbapp_callcenter_agent_call_detail_f_kudu
        WHERE 
        contact_type = 'Outbound'
        AND 
        start_time >= 'start_time'
    ) as call
    INNER JOIN 
    rods.rods_jlbapp_imc_case_f_kudu as case_inf
    ON call.called_number = case_inf.mobile""" \
        .replace("'start_time'", "'" + start_date + "'")
    data = pd.read_sql(sql=sql, con=from_con)
    return data


def get_apply_case_data(from_con, start_date):
    sql = """
        SELECT 
        cs_inf.org_id as org_id,
        cs_apl.case_id as case_id,
        cs_apl.do_order_time as do_order_time,
        cs_inf.follow_employee_id as emp_id
        FROM
        rods.rods_jlbapp_imc_case_apply_f_kudu as cs_apl
        LEFT JOIN
        rods.rods_jlbapp_imc_case_f_kudu as cs_inf
        on cs_apl.case_id = cs_inf.id 
        WHERE cs_apl.do_order_time >= 'start_time' 
    """.replace("'start_time'", "'" + start_date + "'")
    data = pd.read_sql(sql=sql, con=from_con)
    return data


def get_visit_data(from_con, start_date):
    sql = """
        SELECT 
        org_id,
        gmt_create as create_time,
        case_id,
        really_visit_time as really_time,
        visit_status as status,
        opt_emp_id as emp_id,
        '到访' as type
        FROM 
        rods.rods_jlbapp_imc_case_visit_records_f_kudu
        WHERE 
        is_deleted <> 1 
        and gmt_create >= 'start_time'

        union all 

        SELECT 
        org_id,
        create_time,
        case_id,
        update_time as really_time,
        status,
        creator_employee_id as emp_id,
        '试听' as type
        FROM
        rods.rods_jlbapp_imc_course_trial_f_kudu
        WHERE 
        create_time >= 'start_time'
    """.replace("'start_time'", "'" + start_date + "'")
    data = pd.read_sql(sql=sql, con=from_con)
    return data


def get_will_dept_sea_case(from_con):
    sql = """
        SELECT 
        cs.id as case_id,
        cs.follow_time as follow_time,
        get_json_object(cs.custom_info,'$.followerUpdateTime') as followerupdatetime,
        get_json_object(cs.custom_info,'$.ccDeptSeaClaimTime') as ccdeptseaclaimtime,
        cs_dept.id as leads_dept_id,
        cs.status as status
        from
        rods.rods_jlbapp_imc_case_f_kudu as cs
        LEFT JOIN 
        rods.rods_jlbapp_imc_leads_dept_f_kudu as cs_dept
        on cs.dept_id = cs_dept.dept_id
        where 
        cs.status not in (50,60,99,40)
        and 
        cs_dept.id in 
        (SELECT 
        leads_dept_id
        from 
        rods.rods_jlbapp_imc_leads_dept_rule_config_f_kudu
        where 
        is_deleted <> 1
        and
        rule_type=3
        and 
        enabled_flag=1)
        and 
        cs.record_status <> 1
    """
    data = pd.read_sql(sql=sql, con=from_con, parse_dates=['follow_time', 'followerupdatetime', 'ccdeptseaclaimtime'])
    return data


def get_will_huishou_case(from_con):
    sql = """
        SELECT 
        cs.id as case_id,
        cs.follow_time as follow_time,
        get_json_object(cs.custom_info,'$.followerUpdateTime') as followerupdatetime,
        get_json_object(cs.custom_info,'$.quality_id') as quality_id,
        cs_dept.id as leads_dept_id,
        cs.follow_employee_id as emp_id,
        cs.channel_id as channel_id
        from
        rods.rods_jlbapp_imc_case_f_kudu as cs
        LEFT JOIN 
        rods.rods_jlbapp_imc_leads_dept_f_kudu as cs_dept
        on cs.dept_id = cs_dept.dept_id
        where 
        cs.status not in (50,60,99,40)
        and 
        cs_dept.id in 
        (SELECT 
        leads_dept_id
        from 
        rods.rods_jlbapp_imc_leads_dept_rule_config_f_kudu
        where 
        is_deleted <> 1
        and
        rule_type=2
        and 
        enabled_flag=1)
        and 
        cs.record_status <> 1
    """
    data = pd.read_sql(sql=sql, con=from_con, parse_dates=['follow_time', 'followerupdatetime'])
    return data


def get_will_brand_sea_case(from_con):
    sql = """
        SELECT 
        cs.org_id as org_id,
        cs.id as case_id,
        cs.follow_time as follow_time,
        get_json_object(cs.custom_info,'$.ccDeptChangeTime') as ccdeptchangetime,
        get_json_object(cs.custom_info,'$.brandSeaClaimTime') as brandseaclaimtime,
        cs_dept.id as leads_dept_id,
        cs.status as status
        from
        rods.rods_jlbapp_imc_case_f_kudu as cs
        LEFT JOIN 
        rods.rods_jlbapp_imc_leads_dept_f_kudu as cs_dept
        on cs.dept_id = cs_dept.dept_id
        where 
        cs.status not in (50,60,99,40)
        and 
        cs.org_id in 
        (SELECT 
        org_id
        from 
        rods.rods_jlbapp_imc_brand_rule_f_kudu
        where 
        (deleted <> 1 or deleted is NULL)
        and
        dept_type=0
        and
        enabled_flag=1
        )
        and 
        cs.record_status <> 1
        """
    data = pd.read_sql(sql=sql, con=from_con, parse_dates=['follow_time', 'ccdeptchangetime', 'brandseaclaimtime'])
    return data


def get_dept_sea_rule(from_con):
    sql = """
        SELECT 
        leads_dept_id,
        rule_detail
        from 
        rods.rods_jlbapp_imc_leads_dept_rule_config_f_kudu
        where 
        is_deleted <> 1
        and
        rule_type=3
        and 
        enabled_flag=1
    """
    data = pd.read_sql(sql=sql, con=from_con).set_index('leads_dept_id')['rule_detail'].to_dict()
    result = {}
    for i in data.keys():
        result[i] = json.loads(data[i])
    return result


def get_brand_sea_rule(from_con):
    sql = """
        SELECT 
        org_id
        ,rule_detail
        from 
        rods.rods_jlbapp_imc_brand_rule_f_kudu
        where 
        (deleted <> 1 or deleted is NULL)
        and
        dept_type=0
        and
        enabled_flag=1
        """
    data = pd.read_sql(sql=sql, con=from_con).set_index('org_id')['rule_detail'].to_dict()
    result = {}
    for i in data.keys():
        result[i] = json.loads(data[i])
    return result


# 计算
def month_type(time):
    this_month = arrow.now().datetime.strftime('%Y-%m')
    last_month = arrow.now().shift(months=-1).datetime.strftime('%Y-%m')
    # last_month = '2020-04'
    try:
        if time.strftime('%Y-%m') == this_month:
            return 'this_month'
        elif time.strftime('%Y-%m') == last_month:
            return 'last_month'
        else:
            return 'not_month'
    except:
        return 'NA'


def week_type(time):
    this_week = arrow.now().datetime.strftime('%Y-%W')
    last_week = arrow.now().shift(weeks=-1).datetime.strftime('%Y-%W')
    # last_month = '2020-04'
    try:
        if time.strftime('%Y-%W') == this_week:
            return 'this_week'
        elif time.strftime('%Y-%W') == last_week:
            return 'last_week'
        else:
            return 'not_week'
    except:
        return 'NA'


def crm_workbench_01(dept_sea_rule, brand_sea_rule, will_dept_sea_case, will_brand_sea_case, from_con,
                     will_huishou_case):
    today = dt.date.today()

    # 判断例子是否即将进入例子库公海
    def will_dept_sea(leads_dept_id, days, followerUpdateTime, follow_time, ccDeptSeaClaimTime, cc_status):
        rule = dept_sea_rule[leads_dept_id]
        if rule['startupTime'] > today.strftime('%Y-%m-%d'):
            return 0
        T1 = today + dt.timedelta(days=days)
        R1, R2, R3, R4 = False, False, False, False
        # rule1
        if 'ccEntryNotFollowed' in rule.keys():
            rule1 = rule['ccEntryNotFollowed']
            day_limit = rule1['dayLimit']
            if followerUpdateTime and follow_time:
                date_rule = max(followerUpdateTime, follow_time)
            else:
                if followerUpdateTime:
                    date_rule = followerUpdateTime
                elif follow_time:
                    date_rule = follow_time
                else:
                    date_rule = None
            # print(type(date_rule).__name__)
            if date_rule is not None and type(date_rule).__name__ != 'NaTType':
                if 'stage' in rule1.keys():
                    if cc_status in rule1['stage'] and (T1 - date_rule.date()).days == day_limit:
                        R1 = True
                else:
                    if (T1 - date_rule.date()).days == day_limit:
                        R1 = True

        if 'ccEntryNotClosed' in rule.keys():
            rule2 = rule['ccEntryNotClosed']
            day_limit = rule2['dayLimit']
            date_rule = followerUpdateTime
            if date_rule is not None and type(date_rule).__name__ != 'NaTType':
                if 'stage' in rule2.keys():
                    if cc_status in rule2['stage']:
                        if 'sinceLastFollow' in rule2.keys():
                            if (T1 - date_rule.date()).days == day_limit and follow_time is not None:
                                if (T1 - follow_time).days >= rule2['sinceLastFollow']:
                                    R2 = True
                        else:
                            if (T1 - date_rule.date()).days == day_limit:
                                R2 = True
                else:
                    if 'sinceLastFollow' in rule2.keys():
                        if (T1 - date_rule.date()).days == day_limit and follow_time is not None:
                            if (T1 - follow_time).days >= rule2['sinceLastFollow']:
                                R2 = True
                    else:
                        if (T1 - date_rule.date()).days == day_limit:
                            R2 = True
        # rule3
        if 'ccClaimNotFollowed' in rule.keys():
            rule3 = rule['ccClaimNotFollowed']
            day_limit = rule3['dayLimit']
            # date_rule = max(ccDeptSeaClaimTime, follow_time)
            if followerUpdateTime and follow_time:
                date_rule = max(ccDeptSeaClaimTime, follow_time)
            else:
                if ccDeptSeaClaimTime:
                    date_rule = ccDeptSeaClaimTime
                elif follow_time:
                    date_rule = follow_time
                else:
                    date_rule = None
            if date_rule is not None and type(date_rule).__name__ != 'NaTType':
                if 'stage' in rule3.keys():
                    if cc_status in rule3['stage'] and date_rule is not None and (
                            T1 - date_rule.date()).days == day_limit:
                        R3 = True
                else:
                    if date_rule is not None and (T1 - date_rule.date()).days == day_limit:
                        R3 = True

        if 'ccClaimNotClosed' in rule.keys():
            rule4 = rule['ccClaimNotClosed']
            day_limit = rule4['dayLimit']
            date_rule = ccDeptSeaClaimTime
            if date_rule is not None and type(date_rule).__name__ != 'NaTType':
                if 'stage' in rule4.keys():
                    if cc_status in rule4['stage']:
                        if 'sinceLastFollow' in rule4.keys():
                            if (T1 - date_rule.date()).days == day_limit and follow_time is not None:
                                if (T1 - follow_time).days >= rule4['sinceLastFollow']:
                                    R4 = True
                        else:
                            if (T1 - date_rule.date()).days == day_limit:
                                R4 = True
                else:
                    if 'sinceLastFollow' in rule4.keys():
                        if (T1 - date_rule.date()).days == day_limit and follow_time is not None:
                            if (T1 - follow_time).days >= rule4['sinceLastFollow']:
                                R4 = True
                    else:
                        if (T1 - date_rule.date()).days == day_limit:
                            R4 = True
        if R1 or R2 or R3 or R4:
            return 1
        else:
            return 0

    # 判断例子是否即将进入品牌公海
    def will_brand_sea(org_id, days, ccDeptChangeTime, follow_time, brandSeaClaimTime, cc_status):
        rule = brand_sea_rule[org_id]
        if rule['startupTime'] > today.strftime('%Y-%m-%d'):
            return 0
        T1 = today + dt.timedelta(days=days)
        R1, R2, R3, R4 = False, False, False, False
        # rule1
        if 'ccEntryNotFollowed' in rule.keys():
            rule1 = rule['ccEntryNotFollowed']
            day_limit = rule1['dayLimit']
            # date_rule = max(ccDeptChangeTime, follow_time)
            if ccDeptChangeTime and follow_time:
                date_rule = max(ccDeptChangeTime, follow_time)
            else:
                if ccDeptChangeTime:
                    date_rule = ccDeptChangeTime
                elif follow_time:
                    date_rule = follow_time
                else:
                    date_rule = None
            if date_rule is not None and type(date_rule).__name__ != 'NaTType':
                if 'stage' in rule1.keys():
                    if cc_status in rule1['stage'] and (T1 - date_rule).days == day_limit:
                        R1 = True
                else:
                    if (T1 - date_rule).days == day_limit:
                        R1 = True
        # rule2
        if 'ccEntryNotClosed' in rule.keys():
            rule2 = rule['ccEntryNotClosed']
            day_limit = rule2['dayLimit']
            date_rule = ccDeptChangeTime
            if date_rule is not None and type(date_rule).__name__ != 'NaTType':
                if 'stage' in rule2.keys():
                    if cc_status in rule2['stage']:
                        if 'sinceLastFollow' in rule2.keys():
                            if (T1 - date_rule).days == day_limit and follow_time is not None:
                                if (T1 - follow_time).days >= rule2['sinceLastFollow']:
                                    R2 = True
                        else:
                            if (T1 - date_rule).days == day_limit:
                                R2 = True
                else:
                    if 'sinceLastFollow' in rule2.keys():
                        if (T1 - date_rule).days == day_limit and follow_time is not None:
                            if (T1 - follow_time).days >= rule2['sinceLastFollow']:
                                R2 = True
                    else:
                        if (T1 - date_rule).days == day_limit:
                            R2 = True
        # rule3
        if 'ccClaimNotFollowed' in rule.keys():
            rule3 = rule['ccClaimNotFollowed']
            day_limit = rule3['dayLimit']
            # date_rule = max(brandSeaClaimTime, follow_time)
            if brandSeaClaimTime and follow_time:
                date_rule = max(brandSeaClaimTime, follow_time)
            else:
                if brandSeaClaimTime:
                    date_rule = brandSeaClaimTime
                elif follow_time:
                    date_rule = follow_time
                else:
                    date_rule = None
            if date_rule is not None and type(date_rule).__name__ != 'NaTType':
                if 'stage' in rule3.keys():
                    if cc_status in rule3['stage'] and date_rule is not None and (
                            T1 - date_rule.date()).days == day_limit:
                        R3 = True
                else:
                    if date_rule is not None and (T1 - date_rule.date()).days == day_limit:
                        R3 = True
        # rule4
        if 'ccClaimNotClosed' in rule.keys():
            rule4 = rule['ccClaimNotClosed']
            day_limit = rule4['dayLimit']
            date_rule = brandSeaClaimTime
            if date_rule is not None and type(date_rule).__name__ != 'NaTType':
                if 'stage' in rule4.keys():
                    if cc_status in rule4['stage']:
                        if 'sinceLastFollow' in rule4.keys():
                            if (T1 - date_rule).days == day_limit and follow_time is not None:
                                if (T1 - follow_time).days >= rule4['sinceLastFollow']:
                                    R4 = True
                        else:
                            if (T1 - date_rule).days == day_limit:
                                R4 = True
                else:
                    if 'sinceLastFollow' in rule4.keys():
                        if (T1 - date_rule).days == day_limit and follow_time is not None:
                            if (T1 - follow_time).days >= rule4['sinceLastFollow']:
                                R4 = True
                    else:
                        if (T1 - date_rule).days == day_limit:
                            R4 = True
        if R1 or R2 or R3 or R4:
            return 1
        else:
            return 0

    # 例子库公海判断
    dept_case = will_dept_sea_case

    def dept(x, days):
        try:
            result = will_dept_sea(x['leads_dept_id'], days, x['followerupdatetime'],
                                   x['follow_time'],
                                   x['ccdeptseaclaimtime'], x['status'])
        except:
            print(x)
            result = 0
        return result

    # print(dept_case.columns)
    dept_case['to_dept_sea_3d'] = dept_case.apply(lambda x: dept(x, 3), axis=1)
    to_dept_sea_3d = set(dept_case[dept_case.to_dept_sea_3d == 1]['case_id'])
    dept_case['to_dept_sea_7d'] = dept_case.apply(lambda x: dept(x, 7), axis=1)
    to_dept_sea_7d = set(dept_case[dept_case.to_dept_sea_7d == 1]['case_id'])
    # dept_case = dept_case[(dept_case.to_dept_sea_3d == 1) | (dept_case.to_dept_sea_7d == 1)]
    # dept_case = dept_case.set_index('case_id').loc[:, ['to_dept_sea_3d', 'to_dept_sea_7d']]

    # 品牌库公海判断
    brand_case = will_brand_sea_case

    def brand(x, days):
        try:
            result = will_brand_sea(x['org_id'], days, x['ccdeptchangetime'],
                                    x['follow_time'],
                                    x['brandseaclaimtime'], x['status'])
        except:
            print(x)
            result = 0
        return result

    brand_case['to_brand_sea_3d'] = brand_case.apply(lambda x: brand(x, 3), axis=1)
    to_brand_sea_3d = set(brand_case[brand_case.to_brand_sea_3d == 1]['case_id'])
    brand_case['to_brand_sea_7d'] = brand_case.apply(lambda x: brand(x, 7), axis=1)
    to_brand_sea_7d = set(brand_case[brand_case.to_brand_sea_7d == 1]['case_id'])

    # brand_case = brand_case[(brand_case.to_brand_sea_3d == 1) | (brand_case.to_brand_sea_7d == 1)]
    # brand_case = brand_case.set_index('case_id').loc[:, ['to_brand_sea_3d', 'to_brand_sea_7d']]
    # 逾期未跟进 late_follow
    sql = """
        SELECT
        DISTINCT case_id_virtual as case_id
        from
        rods.rods_jlbapp_imc_task_f_kudu
        WHERE
        status = 0
        and task_time<now()
    """
    # late_follow = pd.read_sql(sql, con=from_con).set_index('case_id').loc[:, ['late_follow']]
    late_follow = set(pd.read_sql(sql, con=from_con)['case_id'])
    # 超过30天未联系 no_contacted_30d
    sql = """
            SELECT
            id as case_id
            from
            rods.rods_jlbapp_imc_case_f_kudu
            WHERE
            status not in (50,60,99,40)
            and (unix_timestamp(now())-unix_timestamp(follow_time))>=30*24*60*60
            and record_status<>1
        """
    no_contacted_30d = set(pd.read_sql(sql, con=from_con)['case_id'])
    print(len(no_contacted_30d))
    # no_contacted_30d = no_contacted_30d.set_index('case_id').loc[:,['no_contacted_30d']]

    # 3天后收回例子

    # 获取回收规则
    sql = """
            SELECT 
            leads_dept_id,
            rule_detail
            from 
            rods.rods_jlbapp_imc_leads_dept_rule_config_f_kudu
            where 
            is_deleted <> 1
            and
            rule_type=2
            and 
            enabled_flag=1
        """
    huishou_rule = pd.read_sql(sql=sql, con=from_con)

    # 判断例子是否会即将回收
    def will_huishou(huishou_rule, leads_dept_id, follow_time, followerupdatetime, channel_id, emp_id, quality_id):
        try:
            rule = huishou_rule[huishou_rule.leads_dept_id == leads_dept_id]
            if type(followerupdatetime).__name__ == 'NaTType' and type(follow_time).__name__ == 'NaTType':
                return 0
            for rl in list(rule['rule_detail']):
                r = json.loads(rl)
                limit = int(r['recoveryHours']) * 3600 + int(r['recoveryMinutes']) * 60
                if r['recoveryType'] == 1:
                    r1 = channel_id in [int(id) for id in r['recoveryKeyIds'].split(',')]
                    r2 = (today + dt.timedelta(days=3)).weekday() in [int(id) for id in r['recoveryDays'].split(',')]
                    if type(followerupdatetime).__name__ == 'NaTType':
                        r3 = False
                    elif type(follow_time).__name__ == 'NaTType' or followerupdatetime > follow_time:
                        r3 = (followerupdatetime + dt.timedelta(seconds=limit)).date() == today + dt.timedelta(days=3)
                    else:
                        r3 = False
                elif r['recoveryType'] == 2:
                    r1 = quality_id in [int(id) for id in r['recoveryKeyIds'].split(',')]
                    r2 = (today + dt.timedelta(days=3)).weekday() in [int(id) for id in r['recoveryDays'].split(',')]
                    if type(followerupdatetime).__name__ == 'NaTType':
                        r3 = False
                    elif type(follow_time).__name__ == 'NaTType' or followerupdatetime > follow_time:
                        r3 = (followerupdatetime + dt.timedelta(seconds=limit)).date() == today + dt.timedelta(days=3)
                    else:
                        r3 = False
                else:
                    r1 = emp_id in r['recoveryKeyIds'].split(',')
                    r2 = (today + dt.timedelta(days=3)).weekday() in [int(id) for id in r['recoveryDays'].split(',')]
                    if type(followerupdatetime).__name__ == 'NaTType':
                        r3 = False
                    elif type(follow_time).__name__ == 'NaTType' or followerupdatetime > follow_time:
                        r3 = (followerupdatetime + dt.timedelta(seconds=limit)).date() == today + dt.timedelta(days=3)
                    else:
                        r3 = False
                if r1 or r2 or r3:
                    return 1
            return 0
        except:
            print(follow_time, followerupdatetime, channel_id, emp_id, quality_id)
            return 0

    # 回收例子判断
    hs_case = will_huishou_case
    hs_case['recover_3d'] = 0
    try:
        hs_case['recover_3d'] = hs_case.apply(
            lambda x: will_huishou(huishou_rule, x['follow_time'], x['leads_dept_id'],
                                   x['followerupdatetime'], x['channel_id'], x['emp_id'], x['quality_id']), axis=1)
    except:
        pass
    recover_3d = set(hs_case[hs_case.recover_3d == 1]['case_id'])
    data_case = to_dept_sea_3d | to_dept_sea_7d | to_brand_sea_3d | to_brand_sea_7d | late_follow | recover_3d | no_contacted_30d
    data = pd.DataFrame()
    data['case_id'] = list(data_case)
    data['to_dept_sea_3d'] = data.case_id.map(lambda x: 1 if x in to_dept_sea_3d else 0)
    data['to_dept_sea_7d'] = data.case_id.map(lambda x: 1 if x in to_dept_sea_7d else 0)
    data['to_brand_sea_3d'] = data.case_id.map(lambda x: 1 if x in to_brand_sea_3d else 0)
    data['to_brand_sea_7d'] = data.case_id.map(lambda x: 1 if x in to_brand_sea_7d else 0)
    data['late_follow'] = data.case_id.map(lambda x: 1 if x in late_follow else 0)
    data['recover_3d'] = data.case_id.map(lambda x: 1 if x in recover_3d else 0)
    data['no_contacted_30d'] = data.case_id.map(lambda x: 1 if x in no_contacted_30d else 0)
    for i in (
    to_dept_sea_3d, to_dept_sea_7d, to_brand_sea_3d, to_brand_sea_7d, late_follow, recover_3d, no_contacted_30d, data):
        print(len(i))

    # 给例子加属性
    sql = """
        SELECT 
        cs.id as case_id,
        cs.org_id,
        cs.follow_employee_id as emp_id,
        dept.department_id as dept_id
         FROM
         rods.rods_jlbapp_imc_case_f_kudu as cs 
        left join 
        rods.rods_jlbapp_imc_leads_dept_f_kudu as dept
        on cs.dept_id=dept.dept_id
        WHERE
        cs.record_status<>1
    """
    case_inf = pd.read_sql(sql=sql, con=from_con)

    data = pd.merge(data, case_inf, on='case_id', how='left')
    data['biztime'] = biztime
    return data


def crm_workbench_02(call_data, apply_case_data, visit_data):
    def data_m():
        g_index = ['org_id', 'school_id', 'emp_id', 'month']
        called_case_cnt = call_data.dropna(subset=['case_id']).pivot_table(index=g_index, values='case_id',
                                                                           aggfunc=lambda x: len(x.unique()))
        try:
            called_case_cnt.columns = ['called_case_cnt']
        except:
            pass
        visit_data['month'] = visit_data['invite_month']
        invited_case_cnt = visit_data.pivot_table(index=g_index, values='case_id',
                                                  aggfunc=lambda x: len(x.unique()))
        try:
            invited_case_cnt.columns = ['invited_case_cnt']
        except:
            pass
        visit_data['month'] = visit_data['really_month']
        visited_case_cnt = visit_data[(visit_data['type'] == '到访') & (visit_data['status'] >= 3)].pivot_table(
            index=g_index,
            values='case_id',
            aggfunc=lambda
                x: len(
                x.unique()))
        try:
            visited_case_cnt.columns = ['visited_case_cnt']
        except:
            pass
        closed_case_cnt = apply_case_data.pivot_table(index=g_index, values='case_id',
                                                      aggfunc=lambda x: len(x.unique()))

        try:
            closed_case_cnt.columns = ['closed_case_cnt']
            # print(closed_case_cnt)
        except:
            pass

        data_m = called_case_cnt.join([invited_case_cnt, visited_case_cnt, closed_case_cnt], how='outer')
        data_m = data_m.fillna(0).reset_index()
        for i in ['called_case_cnt',
                  'invited_case_cnt',
                  'visited_case_cnt',
                  'closed_case_cnt']:

            if i not in data_m.columns.tolist():
                data_m[i] = 0
            data_m[i] = data_m[i].fillna(0).astype(int)

        data_m['called_case_cnt_school_order'] = data_m['called_case_cnt'].groupby(
            [data_m.school_id, data_m.month]).rank(
            ascending=0, method='min')
        data_m['invited_case_cnt_school_order'] = data_m['invited_case_cnt'].groupby(
            [data_m.school_id, data_m.month]).rank(
            ascending=0, method='min')
        data_m['visited_case_cnt_school_order'] = data_m['visited_case_cnt'].groupby(
            [data_m.school_id, data_m.month]).rank(
            ascending=0, method='min')
        data_m['closed_case_cnt_school_order'] = data_m['closed_case_cnt'].groupby(
            [data_m.school_id, data_m.month]).rank(
            ascending=0, method='min')

        data_m['called_case_cnt_org_order'] = data_m['called_case_cnt'].groupby([data_m.org_id, data_m.month]).rank(
            ascending=0, method='min')
        data_m['invited_case_cnt_org_order'] = data_m['invited_case_cnt'].groupby([data_m.org_id, data_m.month]).rank(
            ascending=0, method='min')
        data_m['visited_case_cnt_org_order'] = data_m['visited_case_cnt'].groupby([data_m.org_id, data_m.month]).rank(
            ascending=0, method='min')
        data_m['closed_case_cnt_org_order'] = data_m['closed_case_cnt'].groupby([data_m.org_id, data_m.month]).rank(
            ascending=0, method='min')
        return data_m

    def data_w():
        g_index = ['org_id', 'school_id', 'emp_id', 'week']
        called_case_cnt = call_data.dropna(subset=['case_id']).pivot_table(index=g_index, values='case_id',
                                                                           aggfunc=lambda x: len(x.unique()))
        try:
            called_case_cnt.columns = ['called_case_cnt']
        except:
            pass
        visit_data['week'] = visit_data['invite_week']
        invited_case_cnt = visit_data.pivot_table(index=g_index, values='case_id',
                                                  aggfunc=lambda x: len(x.unique()))
        try:
            invited_case_cnt.columns = ['invited_case_cnt']
        except:
            pass
        visit_data['week'] = visit_data['really_week']
        visited_case_cnt = visit_data[(visit_data['type'] == '到访') & (visit_data['status'] >= 3)].pivot_table(
            index=g_index,
            values='case_id',
            aggfunc=lambda
                x: len(
                x.unique()))
        try:
            visited_case_cnt.columns = ['visited_case_cnt']
        except:
            pass
        closed_case_cnt = apply_case_data.pivot_table(index=g_index, values='case_id',
                                                      aggfunc=lambda x: len(x.unique()))
        try:
            closed_case_cnt.columns = ['closed_case_cnt']
        except:
            pass

        data_w = called_case_cnt.join([invited_case_cnt, visited_case_cnt, closed_case_cnt], how='outer')

        data_w = data_w.fillna(0).reset_index()

        for i in ['called_case_cnt',
                  'invited_case_cnt',
                  'visited_case_cnt',
                  'closed_case_cnt']:

            if i not in data_w.columns.tolist():
                data_w[i] = 0
            data_w[i] = data_w[i].fillna(0).astype(int)
        data_w['called_case_cnt_school_order'] = data_w['called_case_cnt'].groupby(
            [data_w.school_id, data_w.week]).rank(
            ascending=0, method='min')
        data_w['invited_case_cnt_school_order'] = data_w['invited_case_cnt'].groupby(
            [data_w.school_id, data_w.week]).rank(
            ascending=0, method='min')
        data_w['visited_case_cnt_school_order'] = data_w['visited_case_cnt'].groupby(
            [data_w.school_id, data_w.week]).rank(
            ascending=0, method='min')
        data_w['closed_case_cnt_school_order'] = data_w['closed_case_cnt'].groupby(
            [data_w.school_id, data_w.week]).rank(
            ascending=0, method='min')

        data_w['called_case_cnt_org_order'] = data_w['called_case_cnt'].groupby([data_w.org_id, data_w.week]).rank(
            ascending=0, method='min')
        data_w['invited_case_cnt_org_order'] = data_w['invited_case_cnt'].groupby([data_w.org_id, data_w.week]).rank(
            ascending=0, method='min')
        data_w['visited_case_cnt_org_order'] = data_w['visited_case_cnt'].groupby([data_w.org_id, data_w.week]).rank(
            ascending=0, method='min')
        data_w['closed_case_cnt_org_order'] = data_w['closed_case_cnt'].groupby([data_w.org_id, data_w.week]).rank(
            ascending=0, method='min')
        return data_w

    data = pd.concat([data_m(), data_w()])
    data['biztime'] = biztime
    return data


def crm_workbench_03(call_data, apply_case_data, visit_data):
    g_index = ['dept_id', 'month']
    called_case_cnt = call_data.dropna(subset=['case_id']).pivot_table(index=g_index, values='case_id',
                                                                       aggfunc=lambda x: len(x.unique()))
    try:
        called_case_cnt.columns = ['called_case_cnt']
    except:
        pass
    visit_data['month'] = visit_data['invite_month']
    invited_case_cnt = visit_data.pivot_table(index=g_index, values='case_id',
                                              aggfunc=lambda x: len(x.unique()))
    try:
        invited_case_cnt.columns = ['invited_case_cnt']
    except:
        pass
    visit_data['month'] = visit_data['really_month']
    visited_case_cnt = visit_data[(visit_data['type'] == '到访') & (visit_data['status'] >= 3)].pivot_table(
        index=g_index,
        values='case_id',
        aggfunc=lambda
            x: len(
            x.unique()))
    try:
        visited_case_cnt.columns = ['visited_case_cnt']
    except:
        pass
    closed_case_cnt = apply_case_data.pivot_table(index=g_index, values='case_id', aggfunc=lambda x: len(x.unique()))
    try:
        closed_case_cnt.columns = ['closed_case_cnt']
    except:
        pass
    data = called_case_cnt.join([invited_case_cnt, visited_case_cnt, closed_case_cnt], how='outer')
    for i in ['called_case_cnt',
              'invited_case_cnt',
              'visited_case_cnt',
              'closed_case_cnt']:

        if i not in data.columns.tolist():
            data[i] = 0
        data[i] = data[i].fillna(0).astype(int)

    data = data.fillna(0).reset_index()
    data['biztime'] = arrow.now().format('YYYY-MM-DD HH:mm:ss')
    return data


def crm_workbench_04(call_data, apply_case_data, visit_data):
    g_index = ['org_id', 'dept_id', 'emp_id', 'day_id']
    called_case_cnt = call_data.dropna(subset=['case_id']).pivot_table(index=g_index, values='case_id',
                                                                       aggfunc=lambda x: len(x.unique()))
    try:
        called_case_cnt.columns = ['called_case_cnt']
    except:
        pass

    success_called_case_cnt = call_data[call_data.contact_disposition == 'Success'].dropna(
        subset=['case_id']).pivot_table(index=g_index, values='case_id',
                                        aggfunc=lambda x: len(x.unique()))
    try:
        success_called_case_cnt.columns = ['success_called_case_cnt']
    except:
        pass

    visit_data['day_id'] = visit_data['invite_day']
    invited_case_cnt = visit_data.pivot_table(index=g_index, values='case_id',
                                              aggfunc=lambda x: len(x.unique()))
    try:
        invited_case_cnt.columns = ['invited_case_cnt']
    except:
        pass
    visit_data['day_id'] = visit_data['really_day']
    visited_case_cnt = visit_data[(visit_data['type'] == '到访') & (visit_data['status'] >= 3)].pivot_table(
        index=g_index,
        values='case_id',
        aggfunc=lambda
            x: len(
            x.unique()))
    try:
        visited_case_cnt.columns = ['visited_case_cnt']
    except:
        pass
    audition_case_cnt = visit_data[(visit_data['type'] == '试听') & (visit_data['status'] == 2)].pivot_table(
        index=g_index,
        values='case_id',
        aggfunc=lambda
            x: len(
            x.unique()))
    try:
        audition_case_cnt.columns = ['audition_case_cnt']
    except:
        pass
    closed_case_cnt = apply_case_data.pivot_table(index=g_index, values='case_id', aggfunc=lambda x: len(x.unique()))

    try:
        closed_case_cnt.columns = ['closed_case_cnt']
    except:
        pass
    data = called_case_cnt.join(
        [invited_case_cnt, success_called_case_cnt, visited_case_cnt, closed_case_cnt, audition_case_cnt], how='outer')

    for i in ['called_case_cnt',
              'invited_case_cnt',
              'success_called_case_cnt',
              'visited_case_cnt',
              'closed_case_cnt',
              'audition_case_cnt']:

        if i not in data.columns.tolist():
            data[i] = 0
        data[i] = data[i].fillna(0).astype(int)

    data = data.fillna(0).reset_index()
    data1 = data[data.day_id >= (dt.date.today() - dt.timedelta(days=30)).strftime('%Y-%m-%d')].pivot_table(
        index='org_id',
        values=['called_case_cnt',
                'invited_case_cnt',
                'success_called_case_cnt',
                'visited_case_cnt',
                'closed_case_cnt',
                'audition_case_cnt'],
        aggfunc=[np.max, np.mean])
    data1.columns = ['org_' + str(s1).replace('mean', 'avg').replace('amax', 'max') + '_' + s2 for (s1, s2) in
                     data1.columns.tolist()]
    data1.reset_index(inplace=True)
    data = pd.merge(data, data1, left_on='org_id', right_on='org_id', how='left')

    dke_1 = data.drop_duplicates(['dept_id', 'emp_id']).loc[:, ['emp_id', 'dept_id']]
    dke_1['value'] = 1
    dke_2 = pd.DataFrame(pd.date_range(dt.date(dt.date.today().year, dt.date.today().month - 1, 1).strftime('%Y-%m-%d'),
                                       dt.date.today()))
    dke_2.columns = ['day_id']
    dke_2['value'] = 1
    dke = pd.merge(dke_1, dke_2, how='left', on='value')
    dke.day_id = dke.day_id.astype(str)
    del dke['value']
    data = pd.merge(data, dke, on=['dept_id', 'emp_id', 'day_id'], how='outer').fillna(0)
    data['biztime'] = arrow.now().format('YYYY-MM-DD HH:mm:ss')

    return data


def crm_workbench_05(call_data, apply_case_data, visit_data):
    g_index = ['dept_id', 'day_id']
    called_case_cnt = call_data.dropna(subset=['case_id']).pivot_table(index=g_index, values='case_id',
                                                                       aggfunc=lambda x: len(x.unique()))
    try:
        called_case_cnt.columns = ['called_case_cnt']
    except:
        pass
    visit_data['day_id'] = visit_data['invite_day']
    invited_case_cnt = visit_data.pivot_table(index=g_index, values='case_id',
                                              aggfunc=lambda x: len(x.unique()))
    try:
        invited_case_cnt.columns = ['invited_case_cnt']
    except:
        pass
    visit_data['day_id'] = visit_data['really_day']
    visited_case_cnt = visit_data[(visit_data['type'] == '到访') & (visit_data['status'] >= 3)].pivot_table(
        index=g_index,
        values='case_id',
        aggfunc=lambda
            x: len(
            x.unique()))
    try:
        visited_case_cnt.columns = ['visited_case_cnt']
    except:
        pass
    closed_case_cnt = apply_case_data.pivot_table(index=g_index, values='case_id', aggfunc=lambda x: len(x.unique()))
    try:
        closed_case_cnt.columns = ['closed_case_cnt']
    except:
        pass
    data = called_case_cnt.join([invited_case_cnt, visited_case_cnt, closed_case_cnt], how='outer')
    for i in ['called_case_cnt',
              'invited_case_cnt',
              'visited_case_cnt',
              'closed_case_cnt']:

        if i not in data.columns.tolist():
            data[i] = 0
        data[i] = data[i].fillna(0).astype(int)

    data = data.fillna(0).reset_index()
    dke_1 = data.drop_duplicates(['dept_id']).loc[:, ['dept_id']]
    dke_1['value'] = 1
    dke_2 = pd.DataFrame(pd.date_range(dt.date(dt.date.today().year, dt.date.today().month - 1, 1).strftime('%Y-%m-%d'),
                                       dt.date.today()))
    dke_2.columns = ['day_id']
    dke_2['value'] = 1
    dke = pd.merge(dke_1, dke_2, how='left', on='value')
    dke.day_id = dke.day_id.astype(str)
    del dke['value']
    data = pd.merge(data, dke, on=['dept_id', 'day_id'], how='outer').fillna(0)
    data['biztime'] = arrow.now().format('YYYY-MM-DD HH:mm:ss')

    return data


def to_gp(data, table_name, gp_con, indexs, all_int=False):
    dtypedict = {}
    for i, j in zip(data.columns, data.dtypes):
        if "object" in str(j):
            dtypedict.update({i: VARCHAR(length=255)})
        elif "float" in str(j) and all_int:
            dtypedict.update({i: Integer()})
        elif "int" in str(j):
            dtypedict.update({i: Integer()})
        else:
            print('ddddddddd:', str(j))

    data.to_sql(table_name, gp_con, if_exists='replace',
                index=False, schema='ads', method='multi', dtype=dtypedict, chunksize=1000)
    for i in indexs:
        gp_con.execute('create index %s_%s_index on ads.%s using btree (%s);' % (table_name, i, table_name, i))


if __name__ == "__main__":
    s_time = dt.datetime.now()
    # 数据库连接信息
    gp_con = create_engine('postgresql+psycopg2://gpadmin@10.10.90.182:5432/data_api')


    def connect_ipl():
        ipl_con = connect(host='10.10.90.183', port=21050, auth_mechanism='NOSASL')
        # 刷新impala
        # ipl_con.cursor().execute('invalidate metadata')
        return ipl_con


    connect_ipl().cursor().execute('invalidate metadata')

    # 当前时间
    biztime = arrow.now().format('YYYY-MM-DD HH:mm:ss')
    # 数据截取时间
    f_start_date = dt.date(dt.date.today().year, dt.date.today().month - 1, 1).strftime('%Y-%m-%d')


    # 取数

    def do_emp_inf():
        global emp_inf
        ipl_con = connect_ipl()
        emp_inf = get_emp_inf(ipl_con)
        print('emp_inf is geted!')


    def do_call_data():
        global call_data
        ipl_con = connect_ipl()
        call_data = get_call_data(ipl_con, f_start_date)
        print('call_data is geted!')


    def do_apply_case_data():
        global apply_case_data
        ipl_con = connect_ipl()
        apply_case_data = get_apply_case_data(ipl_con, f_start_date)
        print('apply_case_data is geted!')


    def do_visit_data():
        global visit_data
        ipl_con = connect_ipl()
        visit_data = get_visit_data(ipl_con, f_start_date)
        print('visit_data is geted!')


    def do_dept_sea_rule():
        global dept_sea_rule
        ipl_con = connect_ipl()
        dept_sea_rule = get_dept_sea_rule(ipl_con)
        print('dept_sea_rule is geted!')


    def do_will_dept_sea_case():
        global will_dept_sea_case
        ipl_con = connect_ipl()
        will_dept_sea_case = get_will_dept_sea_case(ipl_con)
        print('will_dept_sea_case is geted!')


    def do_brand_sea_rule():
        global brand_sea_rule
        ipl_con = connect_ipl()
        brand_sea_rule = get_brand_sea_rule(ipl_con)
        print('brand_sea_rule is geted!')


    def do_will_brand_sea_case():
        global will_brand_sea_case
        ipl_con = connect_ipl()
        will_brand_sea_case = get_will_brand_sea_case(ipl_con)
        print('will_brand_sea_case is geted!')


    def do_will_huishou_case():
        global will_huishou_case
        ipl_con = connect_ipl()
        will_huishou_case = get_will_huishou_case(ipl_con)
        print('will_huishou_case is geted!')


    thread_lst = []
    thread_lst.append(th.Thread(target=do_emp_inf, ))
    thread_lst.append(th.Thread(target=do_call_data, ))
    thread_lst.append(th.Thread(target=do_apply_case_data, ))
    thread_lst.append(th.Thread(target=do_visit_data, ))
    thread_lst.append(th.Thread(target=do_dept_sea_rule, ))
    thread_lst.append(th.Thread(target=do_will_dept_sea_case, ))
    thread_lst.append(th.Thread(target=do_brand_sea_rule, ))
    thread_lst.append(th.Thread(target=do_will_brand_sea_case, ))
    thread_lst.append(th.Thread(target=do_will_huishou_case, ))

    for t in thread_lst:
        t.start()
    for t in thread_lst:
        t.join()
    print('数据读取完成!')
    call_data['month'] = call_data.start_time.map(month_type)
    call_data['week'] = call_data.start_time.map(week_type)
    call_data['day_id'] = call_data.start_time.dropna().map(lambda x: x.strftime('%Y-%m-%d'))

    apply_case_data['month'] = apply_case_data.do_order_time.map(month_type)
    apply_case_data['week'] = apply_case_data.do_order_time.map(week_type)
    apply_case_data['day_id'] = apply_case_data.do_order_time.dropna().map(lambda x: x.strftime('%Y-%m-%d'))

    visit_data['invite_month'] = visit_data.create_time.map(month_type)
    visit_data['invite_week'] = visit_data.create_time.map(week_type)
    visit_data['invite_day'] = visit_data.create_time.dropna().map(lambda x: x.strftime('%Y-%m-%d'))

    visit_data['really_month'] = visit_data.really_time.map(month_type)
    visit_data['really_week'] = visit_data.really_time.map(week_type)
    visit_data['really_day'] = visit_data.really_time.dropna().map(lambda x: x.strftime('%Y-%m-%d'))

    call_data = pd.merge(call_data, emp_inf, left_on='emp_id', right_on='emp_id', how='left')
    apply_case_data = pd.merge(apply_case_data, emp_inf, left_on='emp_id', right_on='emp_id', how='left')
    visit_data = pd.merge(visit_data, emp_inf, left_on='emp_id', right_on='emp_id', how='left')

    print('数据预处理完成!')


    def do_crm_workbench_01():
        data = crm_workbench_01(dept_sea_rule, brand_sea_rule, will_dept_sea_case, will_brand_sea_case, connect_ipl(),
                                will_huishou_case)
        indexs = ['org_id', 'emp_id', 'dept_id']
        to_gp(data, 'inf_crm_workbench_01_f_2h', gp_con, indexs, True)
        print('crm_workbench_01 is done!')


    def do_crm_workbench_02():
        data = crm_workbench_02(call_data, apply_case_data, visit_data)
        indexs = ['org_id', 'emp_id', 'school_id', 'week', 'month']
        to_gp(data, 'inf_crm_workbench_02_f_2h', gp_con, indexs, True)
        print('crm_workbench_02 is done!')


    def do_crm_workbench_03():
        data = crm_workbench_03(call_data, apply_case_data, visit_data)
        indexs = ['dept_id', 'month']
        to_gp(data, 'inf_crm_workbench_03_f_2h', gp_con, indexs,
              True)
        print('crm_workbench_03 is done!')


    def do_crm_workbench_04():
        data = crm_workbench_04(call_data, apply_case_data, visit_data)
        indexs = ['org_id', 'dept_id', 'emp_id', 'day_id']
        to_gp(data, 'inf_crm_workbench_04_f_2h', gp_con, indexs,
              True)
        print('crm_workbench_04 is done!')


    def do_crm_workbench_05():
        data = crm_workbench_05(call_data, apply_case_data, visit_data)
        indexs = ['dept_id', 'day_id']
        to_gp(data, 'inf_crm_workbench_05_f_2h', gp_con, indexs,
              True)
        print('crm_workbench_05 is done!')

    thread_lst = []
    thread_lst.append(th.Thread(target=do_crm_workbench_01, ))
    thread_lst.append(th.Thread(target=do_crm_workbench_02, ))
    thread_lst.append(th.Thread(target=do_crm_workbench_03, ))
    thread_lst.append(th.Thread(target=do_crm_workbench_04, ))
    thread_lst.append(th.Thread(target=do_crm_workbench_05, ))

    for t in thread_lst:
        t.start()
    for t in thread_lst:
        t.join()
    print('数据计算完成且已落库!')
    print('总用时', dt.datetime.now() - s_time)
