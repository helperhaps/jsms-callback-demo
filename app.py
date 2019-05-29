import os, json, hashlib
from flask import Flask, request, jsonify


app = Flask(__name__)

appkey='dev_sample_appKey'
app_master_secret='dev_sample_appMasterSecret'

types = [ 'SMS_REPLY', 'SMS_REPORT', 'SMS_TEMPLATE', 'SMS_SIGN' ]


@app.route('/callback', methods=['GET', 'POST'])
def callback():
    if request.method == 'POST':
        nonce       = request.form.get('nonce')
        signature   = request.form.get('signature')
        timestamp   = request.form.get('timestamp')
        typ         = request.form.get('type')
        data_str    = request.form.get('data')
        data        = json.loads(data_str)
        result = {}
        print('Message type is {}'.format(typ))

        if typ in types and valid_signature(nonce, timestamp, signature):
            result['data'] = data
            return jsonify(result)
        else:
            return 'FAILD'
    else:
        return request.args.get('echostr')


def valid_signature(nonce, timestamp, signature):
    raw_str = "appKey={}&appMasterSecret={}&nonce={}&timestamp={}".format(appkey, app_master_secret, nonce, timestamp)
    sign = hashlib.sha1(raw_str.encode('utf-8')).hexdigest()
    return True if signature == sign else False
