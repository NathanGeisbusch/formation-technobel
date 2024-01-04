import requests
import csv
import os

class Api:
    def __init__(self, base_url):
        self.tokens = {}
        self.data = {}
        self.base_url = base_url

    def request(self, uri, method, body=None, token=None):
        headers = {'Content-Type': 'application/json'}
        if token:
            headers['Authorization'] = token

        options = {'headers': headers}

        if body and method != 'get':
            options['json'] = body

        full_uri = f"{self.base_url}{uri}"
        if method == 'get' and body:
            full_uri = f"{self.base_url}{uri}?{requests.compat.urlencode(body)}"

        response = requests.request(method, full_uri, **options)

        try:
            response.raise_for_status()
            if response.text:
                return {'response': response, 'body': response.json()}
            else:
                return {'response': response, 'body': None}
        except requests.exceptions.HTTPError as ex:
            print(uri, method, response.status_code, response.text)
            raise ex

    def get(self, uri, body=None, token=None):
        return self.request(uri, 'get', body, token)

    def post(self, uri, body=None, token=None):
        return self.request(uri, 'post', body, token)

    def put(self, uri, body=None, token=None):
        return self.request(uri, 'put', body, token)

    def patch(self, uri, body=None, token=None):
        return self.request(uri, 'patch', body, token)

    def delete(self, uri, body=None, token=None):
        return self.request(uri, 'delete', body, token)

    def options(self, uri, body=None, token=None):
        return self.request(uri, 'options', body, token)


def main():
    API = Api('http://localhost:8000/api')
    response = None
    body = None

    # LOG AS ADMIN
    response = API.post('/auth/sign-in', {'login': 'admin.playzone@hotmail.com', 'password': 'admin'})
    if response['response'].status_code != 200:
        raise Exception(f"{response['response'].url} {response['response'].status_code}")
    API.tokens['admin'] = response['body']['token']

    # CREATE PROJECT
    PROJECT = {
        "name": "Projet Emplois 2021",
        "description": "Statistiques de l'emploi en 2021"
    }
    response = API.post('/projects', PROJECT, API.tokens['admin'])
    if response['response'].status_code != 201:
        raise Exception(f"{response['response'].url} {response['response'].status_code}")
    API.data['projectId'] = response['body']['id']

    # CREATE TABLES
    print('Creating tables...')
    DTABLES = {
        "DimNace": {
            "table": "DimNace",
            "headers": [
                {"name": "CodeNace"},
                {"name": "CoefficientNace"},
                {"name": "Date"},
                {"name": "SKNace"},
                {"name": "SousSecteur"},
            ]
        },
        "DimGeo": {
            "table": "DimGeo",
            "headers": [
                {"name": "Province"},
                {"name": "Region"},
                {"name": "SKProvince"},
            ]
        },
        "DimStatut": {
            "table": "DimStatut",
            "headers": [
                {"name": "SKStatut"},
                {"name": "StatutLaboral"},
            ]
        },
        "DimDate": {
            "table": "DimDate",
            "headers": [
                {"name": "Annee"},
                {"name": "SKDate"},
            ]
        },
        "FactEffectifs": {
            "table": "FactEffectifs",
            "headers": [
                {"name": "DateFK"},
                {"name": "Effectifs"},
                {"name": "EffectifsPonderes"},
                {"name": "NaceFK"},
                {"name": "ProvinceFK"},
                {"name": "StatutFK"},
            ]
        },
    }
    tables = list(DTABLES.values())
    API.data['tablesId'] = {}
    for table in tables:
        response = API.post('/tables', table, API.tokens['admin'])
        if response['response'].status_code != 201:
            raise Exception(f"{response['response'].url} {response['response'].status_code}")
        API.data['tablesId'][table['table']] = response['body']['id']

    # CREATE SCHEMA
    print('Creating schema...')
    id = API.data['projectId']
    for table in DTABLES.values():
        id2 = API.data['tablesId'][table['table']]
        response = API.post(f'/projects/{id}/schema/tables/{id2}', None, API.tokens['admin'])
        if response['response'].status_code != 204:
            raise Exception(f"{response['response'].url} {response['response'].status_code}")

    # SCHEMA TABLES (fact)
    def set_schema_table(table_name, payload):
        id2 = API.data['tablesId'][table_name]
        response = API.patch(f'/projects/{id}/schema/tables/{id2}', payload, API.tokens['admin'])
        if response['response'].status_code != 204:
            raise Exception(f"{response['response'].url} {response['response'].status_code}")
    set_schema_table('FactEffectifs', {'fact': True})

    # SCHEMA HEADERS
    def set_schema_header(table_name, header_name, payload):
        id2 = API.data['tablesId'][table_name]
        response = API.get(f'/tables/{id2}', None, API.tokens['admin'])
        if response['response'].status_code != 200:
            raise Exception(f"{response['response'].url} {response['response'].status_code}")
        headers = response['body']['headers']
        header_id = next((dh['id'] for dh in headers if dh['name'] == header_name), None)
        if not header_id:
            raise Exception(f"Header ID not found for {header_name} in table {table_name}")
        response = API.patch(f'/projects/{id}/schema/headers/{header_id}', payload, API.tokens['admin'])
        if response['response'].status_code != 204:
            raise Exception(f"{response['response'].url} {response['response'].status_code}")
        return header_id

    date_pk_id = set_schema_header('DimDate', 'SKDate', {'pk': True})
    geo_pk_id = set_schema_header('DimGeo', 'SKProvince', {'pk': True})
    statut_pk_id = set_schema_header('DimStatut', 'SKStatut', {'pk': True})
    nace_pk_id = set_schema_header('DimNace', 'SKNace', {'pk': True})

    set_schema_header('FactEffectifs', 'DateFK', {'fk': {'table': API.data['tablesId']['DimDate'], 'field': date_pk_id}})
    set_schema_header('FactEffectifs', 'ProvinceFK', {'fk': {'table': API.data['tablesId']['DimGeo'], 'field': geo_pk_id}})
    set_schema_header('FactEffectifs', 'StatutFK', {'fk': {'table': API.data['tablesId']['DimStatut'], 'field': statut_pk_id}})
    set_schema_header('FactEffectifs', 'NaceFK', {'fk': {'table': API.data['tablesId']['DimNace'], 'field': nace_pk_id}})

    # CREATE DATA
    print('Creating data...')
    folder = os.path.dirname(__file__)
    def read_csv(filename):
        with open(folder+filename, newline='') as csvfile:
            return list(csv.DictReader(csvfile))

    DATA = {
        'DimDate': read_csv('/csv/DimDate.csv'),
        'DimGeo': read_csv('/csv/DimGeo.csv'),
        'DimNace': read_csv('/csv/DimNace.csv'),
        'DimStatut': read_csv('/csv/DimStatutTravail.csv'),
        'FactEffectifs': read_csv('/csv/FactEffectifs.csv'),
    }

    datas = list(DATA.items())
    for data in datas:
        key, value = data
        table_id = API.data['tablesId'][key]
        response = API.post(f'/tables/{table_id}/data', value, API.tokens['admin'])
        if response['response'].status_code != 201:
            raise Exception(f"{response['response'].url} {response['response'].status_code}")

    # CREATE VIEWS
    print('Creating views...')
    def get_header_id(table_name, header_name):
        id2 = API.data['tablesId'][table_name]
        response = API.get(f'/tables/{id2}', None, API.tokens['admin'])
        if response['response'].status_code != 200:
            raise Exception(f"{response['response'].url} {response['response'].status_code}")
        headers = response['body']['headers']
        header_id = next((dh['id'] for dh in headers if dh['name'] == header_name), None)
        if not header_id:
            raise Exception(f"Header ID not found for {header_name} in table {table_name}")
        return header_id

    VIEW_1 = {
        "label": {
            "table": API.data['tablesId']['DimStatut'],
            "field": get_header_id('DimStatut', 'StatutLaboral'),
        },
        "data": {
            "table": API.data['tablesId']['DimStatut'],
            "field": get_header_id('DimStatut', 'StatutLaboral'),
        }
    }
    VIEW_2 = {
        "label": {
            "table": API.data['tablesId']['DimStatut'],
            "field": get_header_id('DimStatut', 'StatutLaboral'),
        }
    }
    VIEW_3 = {
        "label": {
            "table": API.data['tablesId']['DimGeo'],
            "field": get_header_id('DimGeo', 'Region'),
        }
    }
    VIEW_4 = {
        "label": {
            "table": API.data['tablesId']['DimGeo'],
            "field": get_header_id('DimGeo', 'Province'),
        }
    }
    VIEW_5 = {
        "label": {
            "table": API.data['tablesId']['DimGeo'],
            "field": get_header_id('DimGeo', 'Region'),
        },
        "data": {
            "table": API.data['tablesId']['DimGeo'],
            "field": get_header_id('DimGeo', 'Province'),
        }
    }
    VIEW_6 = {
        "label": {
            "table": API.data['tablesId']['DimGeo'],
            "field": get_header_id('DimGeo', 'Province'),
        },
        "data": {
            "table": API.data['tablesId']['DimStatut'],
            "field": get_header_id('DimStatut', 'StatutLaboral'),
        }
    }
    VIEW_7 = {
        "label": {
            "table": API.data['tablesId']['DimGeo'],
            "field": get_header_id('DimGeo', 'Province'),
        }
    }
    VIEW_8 = {
        "label": {
            "table": API.data['tablesId']['DimGeo'],
            "field": get_header_id('DimGeo', 'Province'),
        },
        "data": {
            "table": API.data['tablesId']['DimStatut'],
            "field": get_header_id('DimStatut', 'StatutLaboral'),
            "pkValue": "0",
        }
    }
    VIEW_9 = {
        "label": {
            "table": API.data['tablesId']['DimGeo'],
            "field": get_header_id('DimGeo', 'Region'),
        },
        "data": {
            "table": API.data['tablesId']['DimNace'],
            "field": get_header_id('DimNace', 'SousSecteur'),
        }
    }
    VIEW_10 = {
        "label": {
            "table": API.data['tablesId']['DimGeo'],
            "field": get_header_id('DimGeo', 'Region'),
        },
        "data": {
            "table": API.data['tablesId']['DimNace'],
            "field": get_header_id('DimNace', 'SousSecteur'),
            "value": "Commerce Detail",
        }
    }
    VIEW_11 = {
        "label": {
            "table": API.data['tablesId']['DimGeo'],
            "field": get_header_id('DimGeo', 'Region'),
        },
        "data": {
            "table": API.data['tablesId']['DimNace'],
            "field": get_header_id('DimNace', 'SousSecteur'),
            "pkValue": "0",
            "value": "Commerce Detail",
        }
    }
    VIEW_12 = {
        "label": {
            "table": API.data['tablesId']['DimGeo'],
            "field": get_header_id('DimGeo', 'Province'),
        },
        "data": {
            "table": API.data['tablesId']['DimStatut'],
            "field": get_header_id('DimStatut', 'StatutLaboral'),
            "pkValue": "3",
        }
    }
    VIEW_13 = {
        "label": {
            "table": get_header_id('DimGeo', 'Province'),
            "field": get_header_id('DimGeo', 'Province'),
        },
        "data": {
            "table": API.data['tablesId']['DimStatut'],
            "field": get_header_id('DimStatut', 'StatutLaboral'),
        }
    }
    VIEW_14 = {
        "label": {
            "table": API.data['tablesId']['DimGeo'],
            "field": API.data['tablesId']['DimGeo'],
        },
        "data": {
            "table": API.data['tablesId']['DimStatut'],
            "field": get_header_id('DimStatut', 'StatutLaboral'),
        }
    }


    # CHART
    print('\n'+str(VIEW_1))
    response = API.post(f'/projects/{id}/chart', VIEW_1, API.tokens['admin'])
    print(response['body'])

    print('\n'+str(VIEW_2))
    response = API.post(f'/projects/{id}/chart', VIEW_2, API.tokens['admin'])
    print(response['body'])

    print('\n'+str(VIEW_3))
    response = API.post(f'/projects/{id}/chart', VIEW_3, API.tokens['admin'])
    print(response['body'])

    print('\n'+str(VIEW_4))
    response = API.post(f'/projects/{id}/chart', VIEW_4, API.tokens['admin'])
    print(response['body'])

    print('\n'+str(VIEW_5))
    response = API.post(f'/projects/{id}/chart', VIEW_5, API.tokens['admin'])
    print(response['body'])

    response = API.post(f'/projects/{id}/views', VIEW_6, API.tokens['admin'])
    if response['response'].status_code != 201:
        raise Exception(f"{response['response'].url} {response['response'].status_code}")
    viewId1 = response['body']['id']
    print('\n'+str(VIEW_6))
    response = API.get(f'/projects/{id}/views/{viewId1}/chart', None, API.tokens['admin'])
    if response['response'].status_code != 200:
        raise Exception(f"{response['response'].url} {response['response'].status_code}")
    print(response['body'])

    print('\n'+str(VIEW_7))
    response = API.post(f'/projects/{id}/chart', VIEW_7, API.tokens['admin'])
    if response['response'].status_code != 200:
        raise Exception(f"{response['response'].url} {response['response'].status_code}")
    print(response['body'])

    print('\n'+str(VIEW_8))
    response = API.post(f'/projects/{id}/chart', VIEW_8, API.tokens['admin'])
    if response['response'].status_code != 200:
        raise Exception(f"{response['response'].url} {response['response'].status_code}")
    print(response['body'])

    print('\n'+str(VIEW_9))
    response = API.post(f'/projects/{id}/chart', VIEW_9, API.tokens['admin'])
    if response['response'].status_code != 200:
        raise Exception(f"{response['response'].url} {response['response'].status_code}")
    print(response['body'])

    print('\n'+str(VIEW_10))
    response = API.post(f'/projects/{id}/chart', VIEW_10, API.tokens['admin'])
    if response['response'].status_code != 200:
        raise Exception(f"{response['response'].url} {response['response'].status_code}")
    print(response['body'])

    print('\n'+str(VIEW_11))
    response = API.post(f'/projects/{id}/chart', VIEW_11, API.tokens['admin'])
    if response['response'].status_code != 200:
        raise Exception(f"{response['response'].url} {response['response'].status_code}")
    print(response['body'])

    print('\n'+str(VIEW_12))
    try:
        response = API.post(f'/projects/{id}/chart', VIEW_12, API.tokens['admin'])
        print(response['response'].status_code)
        print(response['body'])
    except:
        pass

    print('\n'+str(VIEW_13))
    try:
        response = API.post(f'/projects/{id}/chart', VIEW_13, API.tokens['admin'])
        print(response['response'].status_code)
        print(response['body'])
    except:
        pass

    print('\n'+str(VIEW_14))
    try:
        response = API.post(f'/projects/{id}/chart', VIEW_14, API.tokens['admin'])
        print(response['response'].status_code)
        print(response['body'])
    except:
        pass

    print('\n')
    search_table_id = API.data['tablesId']['DimGeo']
    search_header_id = get_header_id('DimGeo', 'Province')
    response = API.get(f'/tables/{search_table_id}/headers/{search_header_id}/search', {"value": "L"}, API.tokens['admin'])
    print(response['body'])

if __name__ == "__main__":
    main()
