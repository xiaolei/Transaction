import json, io, math
import urllib2

unsupported_ISO4217_currency_code = ['GGP', 'CNH', 'CNT', 'JEP', 'IMP', 'KID', 'SLS', 'SLSH', 'PRB', 'TVD', 'BTC', 'XBT'];

def update():
    data = json.load(urllib2.urlopen('https://openexchangerates.org/api/latest.json?app_id=756bb10cd2ca41e8a80f40a60e425864'))
    with io.open('exchange_rates.txt', 'w', encoding='utf-8') as f:
      f.write(unicode(json.dumps(data, ensure_ascii=False)))
    print(str(data['rates']))

    updated_exchange_rate_count = 0;
    with io.open('exchange_rates.sql', 'w', encoding='utf-8') as f:
      for key, value in data['rates'].iteritems():
        intValue = math.trunc(value*100)
        if intValue <= 0 or (key in unsupported_ISO4217_currency_code):
            continue
        sql = 'INSERT INTO exchange_rate(currency_code, exchange_rate) VALUES("{0}", {1})'.format(key, str(intValue))
        f.write(unicode(sql + ';\r\n'))
        updated_exchange_rate_count = updated_exchange_rate_count + 1
        print('currency code=' + key + ', exchange rate=' + str(intValue) + '\n')
    print('\nupdated exchange rate count: ' + str(updated_exchange_rate_count))


if __name__ == "__main__":
    update()
