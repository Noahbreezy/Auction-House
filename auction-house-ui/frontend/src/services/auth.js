export function getTokenPayload() {
  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    const encodedPayload = token.split('.')[1];
    return JSON.parse(atob(encodedPayload));
  } catch (e) {
    console.error("Failed to decode token", e);
    return null;
  }
}

export function getUserRole() {
  const payload = getTokenPayload();
  return payload?.role || null;
}

export function getUserName() {
  const payload = getTokenPayload();
  return payload?.name || null;
}

export function getUserEmail() {
  const payload = getTokenPayload();
  return payload?.email || null;
}

export function isAuthenticated() {
  return !!localStorage.getItem('token');
}

export function isAdmin() {
  return getUserRole() === 'ADMIN';
}

export function isAuctioneer() {
  return getUserRole() === 'AUCTIONEER';
}

export function isUser() {
  return getUserRole() === 'USER';
}

