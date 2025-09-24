// import React, { useEffect, useState } from 'react';
// import axiosInstance from '../../services/AxiosIntercepteur';

// const UserManagement = () => {
//   const [users, setUsers] = useState([]);
//   const [loading, setLoading] = useState(true);
  
//   useEffect(() => {
//     // Charger les utilisateurs
//     axiosInstance.get('/admin/users')
//       .then(response => {
//         setUsers(response.data);
//         setLoading(false);
//       })
//       .catch(error => {
//         console.error('Erreur lors du chargement des utilisateurs:', error);
//         setLoading(false);
//       });
//   }, []);
  
//   return (
//     <div className="user-management">
//       <h1>Gestion des utilisateurs</h1>
      
//       {loading ? (
//         <p>Chargement des utilisateurs...</p>
//       ) : (
//         <table className="users-table">
//           <thead>
//             <tr>
//               <th>ID</th>
//               <th>Nom</th>
//               <th>Prénom</th>
//               <th>Email</th>
//               <th>Type de compte</th>
//               <th>Actions</th>
//             </tr>
//           </thead>
//           <tbody>
//             {users.map(user => (
//               <tr key={user.id}>
//                 <td>{user.id}</td>
//                 <td>{user.nom}</td>
//                 <td>{user.prenom}</td>
//                 <td>{user.email}</td>
//                 <td>{user.accountType}</td>
//                 <td>
//                   <button>Modifier</button>
//                   <button>Supprimer</button>
//                 </td>
//               </tr>
//             ))}
//           </tbody>
//         </table>
//       )}
//     </div>
//   );
// };

// export default UserManagement;