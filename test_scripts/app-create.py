#! /usr/bin/env python3
import requests, sys, shutil, tempfile, json

app_info = {
    "app_name": "Anonymize data file",
    "language": "PYTHON",
    "params": [
        {
            "name": "algorithm",
            "type": "KEY_VALUE",
            "label": "Algorithm",
            "description": "Algorithm to anonymize dataset"
        },
        {
            "name": "file",
            "type": "FILE",
            "label": "File to anonymize",
            "description": "A csv data file"
        }
    ]
}

code_dir = '../example_apps/python/hello-world'
avatar_url = 'https://hoanghamobile.com/tin-tuc/wp-content/uploads/2019/11/che-do-toi-tren-zalo-1.jpg'

temp_name = tempfile.mkstemp()[1]
zip_file = shutil.make_archive(temp_name, 'zip', root_dir=code_dir, base_dir='./')

r = requests.post('http://localhost:5001/app/create', files={
    'app_info': (None, json.dumps(app_info)),
    'code_file': ('code.zip', open(zip_file, 'rb')),
    'avatar_file': ('avatar', requests.get(avatar_url).content)
})

print(json.dumps(r.json(), indent=4))