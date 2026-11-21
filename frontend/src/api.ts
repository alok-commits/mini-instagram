import { getToken } from './auth';
const API = import.meta.env.VITE_API_URL || 'http://localhost:8080';
export type Profile = { id: string; keycloakUserId: string; username: string; displayName: string; bio: string };
export type Post = { id: string; authorId: string; caption: string; imageUrl: string | null; createdAt: string; version: number };
export type PostsPage = { items: Post[]; page: number; size: number; totalElements: number; totalPages: number };
export type LikeCount = { postId: string; count: number };
export type Problem = { title?: string; detail?: string; errorCode?: string; traceId?: string };
export async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
 const token = await getToken();
 const headers: Record<string,string> = { ...(options.headers as Record<string,string> || {}) };
 if (token) headers.Authorization = `Bearer ${token}`;
 if (options.body) headers['Content-Type'] = 'application/json';
 const res = await fetch(`${API}${path}`, { ...options, headers, cache: 'no-store' });
 if (!res.ok) {
   const error = await res.json().catch(() => ({} as Problem)) as Problem;
   throw new Error(`${error.detail || error.title || res.statusText} (${res.status})`);
 }
 return res.status === 204 ? undefined as T : await res.json() as T;
}
