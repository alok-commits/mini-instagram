#!/usr/bin/env python3
"""Offline static consistency checks; not a replacement for Maven or Docker integration tests."""
import json,xml.etree.ElementTree as ET,re,sys
from pathlib import Path
try: import yaml
except ImportError:
 print('Install PyYAML: python3 -m pip install pyyaml');sys.exit(2)
root=Path(__file__).resolve().parents[1]
def must(condition,message):
 if not condition: raise AssertionError(message)
for file in root.rglob('pom.xml'):
 tree=ET.parse(file);must(tree.getroot().tag.endswith('project'),f'invalid Maven POM {file}')
for file in list(root.rglob('*.yaml'))+list(root.rglob('*.yml')):
 if 'generated' in file.parts:continue
 try:
  if file.name=='compose.yaml':
   data=yaml.safe_load(file.read_text());must(len(data['services'])>=12,'missing containers')
  else:yaml.safe_load(file.read_text())
 except Exception as exc:raise AssertionError(f'Invalid YAML {file}: {exc}') from exc
for file in root.rglob('*.json'):
 if 'generated' not in file.parts and file.name not in ['tsconfig.json']:
  json.loads(file.read_text())
services=['api-gateway','user-service','post-service','like-service']
for service in services:
 must((root/f'services/{service}/src/main/resources/application.yml').exists(),service+' config')
 must((root/f'services/{service}/src/main/resources/logback-spring.xml').exists(),service+' logging')
java_files=list(root.rglob('*.java'))
must(len(java_files)>=34,f'Only {len(java_files)} java files found')
for file in java_files:
 text=file.read_text()
 must(text.count('{')==text.count('}'),f'Unbalanced curly brackets: {file}')
 must('package com.mininstagram.' in text,f'Package missing: {file}')
gateway=yaml.safe_load((root/'services/api-gateway/src/main/resources/application.yml').read_text())
routes=gateway['spring']['cloud']['gateway']['server']['webflux']['routes']
must(len(routes)==6,'expected six gateway route groups')
for route in routes:
 must(any(f.get('name')=='RequestRateLimiter' for f in route['filters']),route['id']+' no rate limiter')
must((root/'infrastructure/keycloak/realm.template.json').exists(),'Keycloak realm missing')
must((root/'services/like-service/src/main/java/com/mininstagram/like/client/ServiceTokenProvider.java').exists(),'service auth missing')
# Count only publicly routed controller mappings: user 2, post 5, like 3.
controller_map={
 'services/user-service/src/main/java/com/mininstagram/user/controller/UserProfileController.java':2,
 'services/post-service/src/main/java/com/mininstagram/post/controller/PostController.java':5,
 'services/like-service/src/main/java/com/mininstagram/like/controller/LikeController.java':3,
}
for relative, expected in controller_map.items():
 count=len(re.findall(r'@(GetMapping|PatchMapping|PostMapping|PutMapping|DeleteMapping)\b',(root/relative).read_text()))
 must(count==expected,f'Expected {expected} endpoints in {relative}; found {count}')
print(f'PASS: {len(java_files)} Java files, {len(routes)} gateway routes, 10 public API methods, YAML/XML/JSON validated')
