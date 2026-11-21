# REST API Contracts

Base URL: `http://localhost:8080/api/v1`. All JSON requests use `Content-Type: application/json`. Mutations require a Keycloak **access token** (not an ID token). Token `aud` includes `instagram-api`; subject `sub` identifies the owner.

| Method | URL | Request | Success |
|---|---|---|---|
| GET | `/users/me` | Bearer token | 200 profile, auto-provisioned on first access |
| PATCH | `/users/me` | `{ "displayName": "Alice", "bio": "Hello" }` | 200 profile |
| POST | `/posts` | `{ "caption": "First post", "imageUrl": null }` | 201 created post + Location |
| GET | `/posts?page=0&size=20` | Query params page 0.., size 1..100 | 200 page envelope |
| GET | `/posts/{postId}` | UUID path | 200 post |
| PATCH | `/posts/{postId}` | `{ "caption": "Edited" }` | 200 post |
| DELETE | `/posts/{postId}` | UUID path | 204 empty |
| PUT | `/posts/{postId}/likes/me` | No body | 204 empty |
| DELETE | `/posts/{postId}/likes/me` | No body | 204 empty |
| GET | `/posts/{postId}/likes/count` | UUID path | 200 `{ "postId": "...", "count": 2 }` |

User profile example:

```json
{
  "id": "17b4b72f-1e1f-4ba2-92c9-5822d4d42a26",
  "keycloakUserId": "c80bdcd4-73cd-46e6-9ee0-d1b7b954f9f2",
  "username": "alice",
  "displayName": "Alice",
  "bio": "Hello",
  "createdAt": "2026-11-19T10:00:00Z",
  "updatedAt": "2026-11-19T10:00:00Z"
}
```

Posts page example:

```json
{
  "items": [{
    "id": "fce8acb1-f04a-4e7f-91a9-45b68ef1ca37",
    "authorId": "c80bdcd4-73cd-46e6-9ee0-d1b7b954f9f2",
    "caption": "First post",
    "imageUrl": null,
    "createdAt": "2026-11-19T10:01:00Z",
    "updatedAt": "2026-11-19T10:01:00Z",
    "version": 0
  }],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1
}
```

Error responses use RFC 9457 ProblemDetail, e.g. `404 POST_NOT_FOUND`, `403 NOT_POST_OWNER`, `400 VALIDATION_FAILED`, `409 DATA_CONFLICT`, `503 POST_SERVICE_UNAVAILABLE`. A trace ID is attached when available. Framework-provided security error responses may differ.

### Semantics and limits

- `caption` is required, nonblank, up to 2,200 characters; optional image URL must begin with HTTPS and is *not fetched or hosted* by the backend.
- Profile `displayName` optional for PATCH (1..120 characters if present), bio optional (up to 300). Username is Keycloak-derived and immutable in this MVP.
- Post `PATCH` updates only non-null fields. Use a new image URL to replace it; clearing an image is not supported in this MVP.
- `GET /posts` returns newest active posts first and is offset-paginated. Deleted posts are absent.
- `PUT` and `DELETE` like endpoints may be repeated without adding/removing extra rows.
- `GET /likes/count` verifies that the post is active and can return 503 if Post Service is unavailable.
- Gateway rate limiting: public reads approx 120/min with burst 20; user endpoints approx 60/min; post creation 10/min with burst 10; like writes approx 60/min with burst 20. These are **token-bucket average rates**, not fixed calendar-minute windows.
- No `/login`, `/register` or `/refresh-token` endpoints are implemented in Spring Boot. Use standard Keycloak endpoints/JS library.

### How to get a token

Use the React frontend to sign in through Keycloak's Authorization Code + PKCE flow. In browser developer tools, the outbound API request will contain an `Authorization: Bearer ...` header. Do not copy tokens into logs, bug reports or chat transcripts.

For automated tests, provision a dedicated test-only Keycloak OAuth client and obtain test tokens using an appropriate non-interactive grant; do not enable the password grant on the public SPA client.
