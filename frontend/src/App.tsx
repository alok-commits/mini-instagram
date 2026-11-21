import { useCallback, useEffect, useState } from 'react';
import { auth, initializeAuth } from './auth';
import { request, type Post, type Profile, type PostsPage, type LikeCount } from './api';

export default function App() {
 const [ready, setReady] = useState(false);
 const [signedIn, setSignedIn] = useState(false);
 const [profile, setProfile] = useState<Profile | null>(null);
 const [posts, setPosts] = useState<Post[]>([]);
 const [counts, setCounts] = useState<Record<string,number | null>>({});
 const [liked, setLiked] = useState<Record<string,boolean>>({});
 const [page, setPage] = useState(0);
 const [hasMore, setHasMore] = useState(false);
 const [caption, setCaption] = useState('');
 const [imageUrl, setImageUrl] = useState('');
 const [displayName, setDisplayName] = useState('');
 const [bio, setBio] = useState('');
 const [editing, setEditing] = useState<string | null>(null);
 const [editedCaption, setEditedCaption] = useState('');
 const [error, setError] = useState('');
 const [pending, setPending] = useState(false);

 const load = useCallback(async (nextPage: number) => {
   try {
     const response = await request<PostsPage>(`/api/v1/posts?page=${nextPage}&size=10`);
     setPosts(old => nextPage === 0 ? response.items : [...old, ...response.items]);
     setPage(nextPage);
     setHasMore(nextPage + 1 < response.totalPages);
     const allCounts = await Promise.all(response.items.map(async p => {
       try { const result = await request<LikeCount>(`/api/v1/posts/${p.id}/likes/count`); return [p.id, result.count] as const; }
       catch { return [p.id, null] as const; }
     }));
     setCounts(old => ({ ...old, ...Object.fromEntries(allCounts) }));
   } catch (e) { setError((e as Error).message); }
 }, []);
 useEffect(() => {
   initializeAuth()
     .then(async loggedIn => {
       setSignedIn(loggedIn);
       if (loggedIn) {
         const me = await request<Profile>('/api/v1/users/me');
         setProfile(me); setDisplayName(me.displayName); setBio(me.bio || '');
       }
     })
     .catch(e => setError('Authentication unavailable: ' + (e as Error).message))
     .finally(() => { setReady(true); void load(0); });
 }, [load]);
 async function act(work: () => Promise<void>) {
   setError(''); setPending(true);
   try { await work(); } catch (e) { setError((e as Error).message); }
   finally { setPending(false); }
 }
 function createPost() {
   void act(async () => {
     await request('/api/v1/posts', { method: 'POST', body: JSON.stringify({ caption, imageUrl: imageUrl || null }) });
     setCaption(''); setImageUrl(''); await load(0);
   });
 }
 function saveProfile(){
   void act(async () => {
     const updated = await request<Profile>('/api/v1/users/me',{method:'PATCH',body:JSON.stringify({ displayName,bio })});
     setProfile(updated);
   });
 }
 function editPost(id: string){
   void act(async () => {
     await request(`/api/v1/posts/${id}`,{method:'PATCH',body:JSON.stringify({caption:editedCaption})});
     setEditing(null);await load(0);
   });
 }
 function deletePost(id:string){
   if (!window.confirm('Delete this post?'))return;
   void act(async () => {await request(`/api/v1/posts/${id}`,{method:'DELETE'});await load(0);});
 }
 function toggleLike(id:string){
   void act(async () => {
     const next=!liked[id];
     await request(`/api/v1/posts/${id}/likes/me`,{method:next?'PUT':'DELETE'});
     setLiked(old=>({...old,[id]:next}));
     const total=await request<LikeCount>(`/api/v1/posts/${id}/likes/count`);
     setCounts(old=>({...old,[id]:total.count}));
   });
 }
 if(!ready) return <main className="loading">Connecting to Keycloak…</main>;
 return <div className="shell">
   <header className="topbar"><div className="brand">mini<span>instagram</span></div>
     <div className="user-actions">{signedIn
       ? <><span>{profile?.username || auth.tokenParsed?.preferred_username}</span><button className="outline" onClick={()=>void auth.logout()}>Sign out</button></>
       : <><button onClick={()=>void auth.login()}>Sign in</button><button className="outline" onClick={()=>void auth.register()}>Register</button></>}
     </div>
   </header>
   <main className="content">
     {error && <div className="error" role="alert">{error} <button className="text-btn" onClick={()=>setError('')}>Dismiss</button></div>}
     <section className="intro"><h1>Share a moment.</h1><p>A small social app backed by three Spring Boot microservices and Keycloak.</p></section>
     {signedIn && <div className="panels">
       <section className="panel"><h2>Your profile</h2><label>Display name<input value={displayName} maxLength={120} onChange={e=>setDisplayName(e.target.value)}/></label>
         <label>Bio<textarea value={bio} maxLength={300} onChange={e=>setBio(e.target.value)}/></label>
         <button disabled={pending || !displayName.trim()} onClick={saveProfile}>Save profile</button>
       </section>
       <section className="panel"><h2>Create a post</h2><label>Caption<textarea value={caption} maxLength={2200} onChange={e=>setCaption(e.target.value)} placeholder="What happened today?"/></label>
         <label>Image URL (optional, HTTPS)<input type="url" value={imageUrl} maxLength={2048} onChange={e=>setImageUrl(e.target.value)} placeholder="https://…"/></label>
         <button disabled={pending || !caption.trim()} onClick={createPost}>Publish post</button>
       </section>
     </div>}
     {!signedIn && <p className="notice">Posts are public. Sign in to publish and like posts.</p>}
     <section className="feed"><div className="feed-head"><h2>Recent posts</h2><button className="outline" onClick={()=>void load(0)}>Refresh</button></div>
       {!posts.length && <div className="empty">No posts yet. Create the first one.</div>}
       {posts.map(post=><article className="post" key={post.id}>
         <div className="post-top"><span className="avatar">{post.authorId.slice(0,1).toUpperCase()}</span><div><strong>{post.authorId === profile?.keycloakUserId ? profile.username : 'Member'}</strong><small>{new Date(post.createdAt).toLocaleString()}</small></div></div>
         {editing===post.id ? <><textarea value={editedCaption} maxLength={2200} onChange={e=>setEditedCaption(e.target.value)}/><button disabled={pending || !editedCaption.trim()} onClick={()=>editPost(post.id)}>Save edit</button><button className="outline" onClick={()=>setEditing(null)}>Cancel</button></> : <p className="post-caption">{post.caption}</p>}
         {post.imageUrl && <img className="post-image" src={post.imageUrl} alt="Post attachment" loading="lazy" referrerPolicy="no-referrer"/>}
         <div className="post-actions"><button className="outline" disabled={pending||!signedIn} onClick={()=>toggleLike(post.id)}>{liked[post.id]?'Unlike':'Like'} · {counts[post.id] ?? '—'}</button>
           {post.authorId===profile?.keycloakUserId && <><button className="outline" onClick={()=>{setEditing(post.id);setEditedCaption(post.caption);}}>Edit</button><button className="outline danger" onClick={()=>deletePost(post.id)}>Delete</button></>}
         </div>
       </article>)}
       {hasMore && <button className="outline wide" onClick={()=>void load(page+1)}>Load more</button>}
     </section>
   </main><footer>Spring Boot · Keycloak · PostgreSQL · Docker</footer>
 </div>;
}
