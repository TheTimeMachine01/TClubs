# APIs

This document provides a comprehensive list of all the APIs in the Clubs project.

## Authentication Service

### `POST /api/v1/auth/register`

Register a new user.

**Request Body:**

```json
{
  "username": "testuser",
  "password": "password",
  "email": "testuser@example.com",
  "firstName": "Test",
  "lastName": "User"
}
```

**Response:**

```json
{
  "token": "...",
  "user": {
    "id": "...",
    "username": "testuser",
    "email": "testuser@example.com",
    "firstName": "Test",
    "lastName": "User"
  }
}
```

### `POST /api/v1/auth/login`

Login a user.

**Request Body:**

```json
{
  "username": "testuser",
  "password": "password"
}
```

**Response:**

```json
{
  "token": "...",
  "user": {
    "id": "...",
    "username": "testuser",
    "email": "testuser@example.com",
    "firstName": "Test",
    "lastName": "User"
  }
}
```

## User Service

### `GET /api/v1/users/{userId}`

Get a user by their ID.

**Response:**

```json
{
  "id": "...",
  "username": "testuser",
  "email": "testuser@example.com",
  "firstName": "Test",
  "lastName": "User"
}
```

## Club Service

### `POST /api/v1/clubs`

Create a new club.

**Request Body:**

```json
{
  "name": "My Club",
  "description": "This is my new club."
}
```

**Response:**

```json
{
  "clubId": "...",
  "name": "My Club",
  "description": "This is my new club.",
  "ownerId": "...",
  "createdAt": "...",
  "updatedAt": "..."
}
```

### `GET /api/v1/clubs/{clubId}`

Get a club by its ID.

**Response:**

```json
{
  "clubId": "...",
  "name": "My Club",
  "description": "This is my new club.",
  "ownerId": "...",
  "createdAt": "...",
  "updatedAt": "..."
}
```

### `POST /api/v1/clubs/{clubId}/join`

Request to join a club.

**Response:**

```json
{
  "membershipId": "...",
  "clubId": "...",
  "userId": "...",
  "status": "PENDING",
  "role": "MEMBER",
  "joinDate": "..."
}
```

### `POST /api/v1/memberships/{membershipId}/approve`

Approve a membership request.

**Response:**

```json
{
  "membershipId": "...",
  "clubId": "...",
  "userId": "...",
  "status": "ACTIVE",
  "role": "MEMBER",
  "joinDate": "..."
}
```

## Feed Service

### `POST /api/v1/posts`

Create a new post.

**Request Body:**

```json
{
  "clubId": "...",
  "content": "This is a new post."
}
```

**Response:**

```json
{
  "postId": "...",
  "clubId": "...",
  "userId": "...",
  "content": "This is a new post.",
  "createdAt": "...",
  "updatedAt": "..."
}
```

### `GET /api/v1/posts/{postId}`

Get a post by its ID.

**Response:**

```json
{
  "postId": "...",
  "clubId": "...",
  "userId": "...",
  "content": "This is a new post.",
  "createdAt": "...",
  "updatedAt": "..."
}
```

### `POST /api/v1/posts/{postId}/like`

Like a post.

**Response:**

```json
{
  "postId": "...",
  "clubId": "...",
  "userId": "...",
  "content": "This is a new post.",
  "createdAt": "...",
  "updatedAt": "...",
  "likeCount": 1
}
```

## Media Service

### `POST /api/v1/media`

Upload a media file.

**Request:**

The request should be a multipart form data request with a `file` part containing the media file.

**Response:**

```json
{
  "mediaId": "...",
  "fileName": "...",
  "fileSize": "...",
  "mimeType": "...",
  "url": "..."
}
```

