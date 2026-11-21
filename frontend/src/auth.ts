import Keycloak from 'keycloak-js';

export const auth = new Keycloak({
 url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:8180',
 realm: 'mini-instagram', clientId: 'instagram-web'
});
let initialized: Promise<boolean> | undefined;
/** Keycloak.init must only execute once; PKCE state survives redirects in the library. */
export function initializeAuth(): Promise<boolean> {
 initialized ??= auth.init({ onLoad: 'check-sso', pkceMethod: 'S256', checkLoginIframe: false });
 return initialized;
}
export async function getToken(): Promise<string | undefined> {
 if (!auth.authenticated) return undefined;
 try { await auth.updateToken(30); }
 catch { await auth.login(); return undefined; }
 return auth.token;
}
