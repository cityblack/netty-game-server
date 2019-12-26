import pandas as pd
import pymongo
import sys, getopt, os
import math
import json

def addDataToDB(data, collection):
    try:
        for record in data:
            inner_data = {}
            for item in record:
                value = record[item]
                if isinstance(value, float) and math.isnan(value):
                    continue
                if isinstance(value, str):
                    try:
                        inner_data.update({item: json.loads(value)})
                    except Exception as e:
                        inner_data.update({item: value})
                else:
                    inner_data.update({item: value})
            collection.insert_one(inner_data)
    except pymongo.errors.DuplicateKeyError:
        print('record exists')
    except Exception as e:
        print(e)

def clearCollect(client, name):

    print("clear [{0}] collect data".format(name))
    client[name].drop()

def walkFile(path, client):
    for root, dirs, files in os.walk(path):
        for file in files:
            if file.endswith(".xlsx"):
                data = loadFile(os.path.join(root, file))
                collectName = file[:-5]
                clearCollect(client, collectName)
                print("add data to [{0}]".format(collectName))
                addDataToDB(data, client[collectName])

def loadFile(path):
    df = pd.DataFrame(pd.read_excel(path, header=3, skip_blank_lines = True))
    return df.to_dict(orient='records')

if __name__ == '__main__':
    
    opts, args = getopt.getopt(sys.argv[1:],"h:p:",["host:","port:"])
    host = "127.0.0.1"
    port = 27017
    for op, value in opts:
        if "-h" == op or op == "--host":
            host = value
        elif "-p" == op or op == "--port":
            port = value
    print("connect to {0}:{1}".format(host, port))
    client = pymongo.MongoClient(host, int(port))
    
    walkFile("./", client["game"])