import requests
import json
import time
import urllib3

log = '\n'
urllib3.disable_warnings()
url = 'http://127.0.0.1:80/api/getuser'
headers = {
    'user-agent': 'Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1 Edg/93.0.4577.82'
}
s = requests.session()
s.keep_alive = False
dakalist = requests.get(url=url, headers=headers).json()['dakalist']
for dakaitem in dakalist:
    try:
        url = 'https://jiankang.suoeryun.com/api/userManager/passageway/pclogin'
        headers = {
            'Accept': 'application/json, text/plain, */*',
            'Accept-Encoding': 'gzip, deflate, br',
            'Accept-Language': 'zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,zh-TW;q=0.6',
            'Content-Type': 'application/json;charset=UTF-8',
            'Host': 'jiankang.suoeryun.com',
            'Origin': 'https://jiankang.suoeryun.com',
            'Pragma': 'no-cache',
            'Referer': 'https://jiankang.suoeryun.com/login?t=1615046779685',
            'Sec-Fetch-Dest': 'empty',
            'Sec-Fetch-Mode': 'cors',
            'Sec-Fetch-Site': 'same-origin',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36 Edg/88.0.705.81'

        }
        data = {
            'f_number': dakaitem['name'],
            'f_password': dakaitem['password'],
            'f_schoolid': dakaitem['schoolid'],
        }
        data_json = json.dumps(data)
        s = requests.session()
        s.keep_alive = False
        res = requests.post(url=url, headers=headers, data=data_json, timeout=300, verify=False)
        res_text = res.json()
        if 'data' not in res_text:
            print(res_text['message'])
            now = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime())
            log += '{} --- {} \n'.format(now, res_text['message'])
            continue
        url = 'https://jiankang.suoeryun.com/api/outbreakRegistered/createOutbreakRegistered'
        headers = {
            'Accept': 'application/json, text/plain, */*',
            'Accept-Encoding': 'gzip, deflate, br',
            'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6',
            'Authorization': res_text['data']['token'],
            'Cache-Control': 'no-cache',
            'Connection': 'keep-alive',
            'Content-Length': '94',
            'Content-Type': 'application/json;charset=UTF-8',
            'Cookie': 'Hm_lvt_eaa57ca47dacb4ad4f5a257001a3457c=1614944007,1614960188,1615046789,1615133891; JSESSIONID=F5299F907F219EC0DF65C110DB086E6D',
            'Host': 'jiankang.suoeryun.com',
            'Origin': 'https://jiankang.suoeryun.com',
            'Pragma': 'no-cache',
            'Referer': 'https://jiankang.suoeryun.com/login',
            'sec - ch - ua': '"Chromium";v = "92", " Not A;Brand"; v = "99", "Microsoft Edge"; v = "92"',
            'sec - ch - ua - mobile': '?0',
            'Sec-Fetch-Dest': 'empty',
            'Sec-Fetch-Mode': 'cors',
            'Sec-Fetch-Site': 'same-origin',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36 Edg/92.0.902.78'
        }
        data = {
            'f_currentdetailsaddress': dakaitem['address'],
            'f_currentlocation': "61",
            'f_daily_temperature': "36.5",
            'f_is_chest_tightness': 0,
            'f_is_confirm_contact': 0,
            'f_is_confirm_contact_address': "",
            'f_is_confirm_contact_time': "",
            'f_is_confirmed': 0,
            'f_is_cough': 0,
            'f_is_fever': 0,
            'f_is_high_risk_address': "",
            'f_is_high_risk_come': 0,
            'f_is_high_risk_time': "",
            'f_is_nausea_emesis': 0,
            'f_is_quarantine_home': 0,
            'f_is_quarantine_medicine': 0,
            'f_is_rhinitis': 0,
            'f_is_suspected': 0,
            'f_is_suspected_contact': 0,
            'f_is_suspected_contact_time': "",
            'f_see_doctor_explain': "",
            'f_symptom_explain': "",
        }
        data_json = json.dumps(data)
        s = requests.session()
        s.keep_alive = False
        res = requests.post(url=url, headers=headers, data=data_json, verify=False)
        now = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime())
        res = json.loads(res.text)
        log += '{} --- {} \n'.format(now, res)
        print(res)
    except Exception as e:
        now = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime())
        print(e)
        log += '{} --- {} \n'.format(now, e)
try:
    with open('/home/dakaguanli/qiandao/log.txt', 'a') as f:
        f.write(log)
        f.close()
except Exception as e:
    print(e)
