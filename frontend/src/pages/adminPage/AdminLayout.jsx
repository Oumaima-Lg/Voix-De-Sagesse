import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../services/useAuth';
import { Button } from '@mantine/core';

const AdminLayout = ({ children }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { logout, user } = useAuth();

  // Vérifier si le lien est actif
  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <div className="admin-layout">
      <div className="admin-sidebar">
        <div className="admin-logo">
          <h2>VoixDeSagesse Admin</h2>
        </div>
        
        <div className="admin-user">
          <p>Connecté en tant que</p>
          <p className="admin-username">{user.nom} {user.prenom}</p>
        </div>
        
        <nav className="admin-nav">
          <ul>
            <li className={isActive('/admin/dashboard') ? 'active' : ''}>
              <Link to="/admin/dashboard">Tableau de bord</Link>
            </li>
            <li className={isActive('/admin/users') ? 'active' : ''}>
              <Link to="/admin/users">Utilisateurs</Link>
            </li>
            <li className={isActive('/admin/articles') ? 'active' : ''}>
              <Link to="/admin/articles">Articles</Link>
            </li>
          </ul>
        </nav>
        
        <div className="admin-actions">
          <Button onClick={() => logout('Vous avez été déconnecté')} color="red">
            Déconnexion
          </Button>
        </div>
      </div>
      
      <div className="admin-content">
        {children}
      </div>
    </div>
  );
};

export default AdminLayout;