#!/usr/bin/env python3
"""Generate local secrets and a Keycloak realm. Never commit generated artifacts."""
import json,secrets,os
from pathlib import Path
root=Path(__file__).resolve().parents[1]
file=root/'.env'
def password():return secrets.token_urlsafe(33)
fields=['POSTGRES_ADMIN_PASSWORD','KEYCLOAK_DB_PASSWORD','USER_DB_PASSWORD',
        'POST_DB_PASSWORD','LIKE_DB_PASSWORD','KEYCLOAK_ADMIN_PASSWORD',
        'LIKE_CLIENT_SECRET','GRAFANA_ADMIN_PASSWORD']
values={}
if file.exists():
 for line in file.read_text().splitlines():
  if '=' in line and not line.startswith('#'):
   k,v=line.split('=',1);values[k]=v
for key in fields:
 if not values.get(key):values[key]=password()
for key,val in {'KEYCLOAK_PUBLIC_URL':'http://localhost:8180',
 'PUBLIC_WEB_ORIGIN':'http://localhost:5173','PUBLIC_API_URL':'http://localhost:8080',
 'PUBLIC_API_HOST':'api.example.com','PUBLIC_AUTH_HOST':'auth.example.com','PUBLIC_WEB_HOST':'app.example.com',
 'TRACE_SAMPLE_RATE':'1.0'}.items():values.setdefault(key,val)
file.write_text(''.join(f'{k}={v}\n' for k,v in values.items()))
try: os.chmod(file,0o600)
except PermissionError: pass
realm=json.loads((root/'infrastructure/keycloak/realm.template.json').read_text())
web=values['PUBLIC_WEB_ORIGIN'].rstrip('/')
realm['clients'][0]['redirectUris']=[web+'/*']
realm['clients'][0]['webOrigins']=[web]
realm['clients'][1]['secret']=values['LIKE_CLIENT_SECRET']
if web.startswith('https://'):
 realm['verifyEmail']=True # Requires a working SMTP provider in Keycloak before enabling signups.
out=root/'infrastructure/keycloak/generated/mini-instagram-realm.json'
out.parent.mkdir(parents=True,exist_ok=True)
out.write_text(json.dumps(realm,indent=2)+'\n')
try: os.chmod(out,0o600)
except PermissionError:pass
frontend=root/'frontend/.env.local'
frontend.write_text('VITE_API_URL='+values['PUBLIC_API_URL']+'\nVITE_KEYCLOAK_URL='+values['KEYCLOAK_PUBLIC_URL']+'\n')
print('Generated .env, Keycloak realm and frontend/.env.local. Secrets are not printed.')
print('If the realm already exists in Keycloak, import is NOT an update. See README for resets/migrations.')
