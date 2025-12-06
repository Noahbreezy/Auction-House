
import React from 'react';
import { isAuthenticated, getUserName, getUserEmail, getUserRole } from '../services/auth';

function ProfilePage() {
  const name = getUserName();
  const email = getUserEmail();
  const role = getUserRole();

  if (!isAuthenticated()) {
    return <p>You must be logged in to view your profile.</p>;
  }

  return (
    <div className="profile-page">
      <h2>Your Profile</h2>
      <div className="profile-card">
        <p><strong>Name:</strong> {name || "N/A"}</p>
        <p><strong>Email:</strong> {email || "N/A"}</p>
        <p><strong>Role:</strong> {role || "N/A"}</p>
      </div>
    </div>
  );
}

export default ProfilePage;