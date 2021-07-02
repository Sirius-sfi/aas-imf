import requests

url = "http://localhost:8080/rdf-to-aasx"

payload={}
files=[
('mel_rdf_file',('mel_rdf.ttl',open("C:\\Users\\ofschha3\\git\\github\\Sirius-sfi\\aas-imf\\mel-to-aasx\\rdf\\mel_rdf.ttl",'r'),'text/turtle'))
]
headers = {}

response = requests.request("POST", url, headers=headers, data=payload, files=files)

#print(response.text)

file = open("C:\\Users\\ofschha3\\git\\github\\Sirius-sfi\\aas-imf\\mel-to-aasx\\aas\\test.aasx",'wb')
file.write(response.content)
file.close()