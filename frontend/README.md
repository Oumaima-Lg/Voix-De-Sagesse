# VoixDeSagesse - Frontend Application

<div align="center">

![React](https://img.shields.io/badge/React-19.0.0-61DAFB.svg?logo=react)
![Vite](https://img.shields.io/badge/Vite-4.0.15-646CFF.svg?logo=vite)
![Redux](https://img.shields.io/badge/Redux%20Toolkit-2.8.2-764ABC.svg?logo=redux)
![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-4.0.15-38B2AC.svg?logo=tailwind-css)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

**A modern React application for sharing and discovering inspirational content**

[Features](#features) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started) • [Project Structure](#project-structure) • [Backend](#backend)

</div>

---

## 📖 Overview

VoixDeSagesse (Voice of Wisdom) Frontend is a modern, responsive web application built with React 19 and Vite. It provides an intuitive and engaging user interface for users to create, share, and discover inspirational content including wisdom quotes and inspiring stories. The application features a clean design, smooth animations, and real-time interactions.

## ✨ Features

### 🎨 User Interface
- **Modern & Responsive Design** with Tailwind CSS
- **Smooth Animations** using Framer Motion
- **Interactive 3D Elements** with Spline integration
- **Dark/Light Mode Support** (configurable)
- **Mobile-First Approach** for all screen sizes
- **Accessible Components** following WCAG guidelines

### 🔐 Authentication & User Management
- **Secure Login/Registration** with form validation
- **JWT Token Management** with automatic refresh
- **Password Reset** via OTP email verification
- **Protected Routes** based on authentication status
- **Role-Based Access** (User/Admin views)
- **Persistent Sessions** with localStorage sync

### 📝 Content Features
- **Create Articles** (Wisdom quotes & Stories)
- **Rich Content Editor** with category and tag selection
- **Article Feed** with personalized content
- **Search & Filter** functionality
- **Popular Articles** section
- **Saved Articles** bookmark system

### 👥 Social Interactions
- **Follow/Unfollow Users** system
- **Like/Unlike Articles** with real-time updates
- **Comment System** with threaded discussions
- **User Profiles** with statistics
- **Activity Feed** showing followed users' content
- **Share Articles** functionality

### 🛡️ Moderation & Admin
- **Report System** for inappropriate content
- **Admin Dashboard** with statistics
- **User Management** interface
- **Content Moderation** tools
- **Signal Processing** workflow

### 🚀 Performance & UX
- **Lazy Loading** for optimal performance
- **Optimistic UI Updates** for instant feedback
- **Error Boundaries** for graceful error handling
- **Loading States** with skeleton screens
- **Toast Notifications** for user feedback
- **Infinite Scroll** for content feeds

## 🛠️ Tech Stack

### Core Framework
- **React 19.0.0** - Latest React with concurrent features
- **Vite 4.0.15** - Next-generation frontend tooling
- **JavaScript (ES6+)** - Modern JavaScript features

### State Management
- **Redux Toolkit 2.8.2** - Simplified Redux with best practices
- **React Redux 9.2.0** - Official React bindings for Redux
- **Redux Persist** - Persist and rehydrate Redux store

### Routing & Navigation
- **React Router DOM 7.4.1** - Declarative routing for React
- **Protected Routes** - Custom route guards
- **Nested Routes** - Complex routing structures

### Styling & UI
- **Tailwind CSS 4.0.15** - Utility-first CSS framework
- **Mantine Core** - React components library
- **Material-UI (MUI)** - Material Design components
- **Framer Motion** - Production-ready animation library
- **Lucide React** - Beautiful & consistent icons

### HTTP & API
- **Axios 1.9.0** - Promise-based HTTP client
- **Axios Interceptors** - Request/response interceptors
- **JWT Decode** - JWT token decoding

### Additional Libraries
- **React Parallax Tilt** - Parallax tilt effect component
- **Spline React** - 3D design integration
- **React Hook Form** - Performant form validation
- **Date-fns** - Modern date utility library

### Development Tools
- **ESLint** - Code linting
- **PostCSS** - CSS transformations
- **Autoprefixer** - CSS vendor prefixing

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Node.js 18.x** or higher
- **npm 9.x** or **yarn 1.22.x** or higher
- **Git** for version control
- **VoixDeSagesse Backend API** running (see [Backend Repository](https://github.com/Oumaima-Lg/Voix-De-Sagesse-Backend))

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/Oumaima-Lg/Voix-De-Sagesse-Frontend.git
cd Voix-De-Sagesse-Frontend
```

### 2. Install Dependencies

**Using npm:**
```bash
npm install
```

**Using yarn:**
```bash
yarn install
```

### 3. Configure Environment Variables

Create a `.env` file in the root directory:
```env
# API Configuration
VITE_API_BASE_URL=http://localhost:8080
VITE_API_TIMEOUT=60000

# Application Configuration
VITE_APP_NAME=VoixDeSagesse
VITE_APP_VERSION=1.0.0

# Feature Flags (optional)
VITE_ENABLE_ANALYTICS=false
VITE_ENABLE_DEBUG=true
```

### 4. Run the Development Server

**Using npm:**
```bash
npm run dev
```

**Using yarn:**
```bash
yarn dev
```

The application will be available at `http://localhost:5173`

### 5. Build for Production

**Using npm:**
```bash
npm run build
```

**Using yarn:**
```bash
yarn build
```

The production build will be in the `dist` folder.

### 6. Preview Production Build

**Using npm:**
```bash
npm run preview
```

**Using yarn:**
```bash
yarn preview
```

## 🔑 Key Features Implementation

### Authentication Flow
```javascript
// Login Example
import { useDispatch } from 'react-redux';
import { setUser } from './redux/slices/UserSlice';
import { setJwt } from './redux/slices/JwtSlice';

const handleLogin = async (credentials) => {
  const response = await authService.login(credentials);
  dispatch(setUser(response.data));
  dispatch(setJwt(response.data.jwtToken));
  navigate('/articla');
};
```

### Protected Routes
```javascript
// ProtectedRoute Component
const ProtectedRoute = ({ children }) => {
  const user = useSelector((state) => state.user);
  
  if (!user) {
    return <Navigate to="/auth/login" replace />;
  }
  
  return children;
};
```

### API Integration with Axios
```javascript
// Axios Interceptor
axiosInstance.interceptors.request.use((config) => {
  const token = getItem('jwt');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized access
      store.dispatch(removeUser());
      store.dispatch(removeJwt());
      window.location.href = '/auth/login';
    }
    return Promise.reject(error);
  }
);
```

### State Management with Redux
```javascript
// UserSlice Example
const UserSlice = createSlice({
  name: 'user',
  initialState: getItem('user') || null,
  reducers: {
    setUser: (state, action) => {
      setItem('user', action.payload);
      return action.payload;
    },
    updateUser: (state, action) => {
      const updatedUser = { ...state, ...action.payload };
      setItem('user', updatedUser);
      return updatedUser;
    },
    removeUser: () => {
      removeItem('user');
      return null;
    },
  },
});
```

## 🎨 UI/UX Features

### Responsive Design
- Mobile-first approach
- Breakpoints: `sm: 640px`, `md: 768px`, `lg: 1024px`, `xl: 1280px`, `2xl: 1536px`
- Flexible grid layouts with Tailwind CSS

### Animations
- Page transitions with Framer Motion
- Smooth hover effects
- Loading animations and skeleton screens
- Micro-interactions for better UX

### Accessibility
- Semantic HTML structure
- ARIA labels and roles
- Keyboard navigation support
- Screen reader compatibility
- Color contrast compliance

## 🔌 API Integration

The frontend communicates with the backend API through the following services:

### Authentication Endpoints
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /auth/sendOtp/{email}` - Send OTP for password reset
- `GET /auth/verifyOtp/{email}/{otp}` - Verify OTP
- `POST /auth/changePass` - Change password

### User Endpoints
- `GET /users/profile/{userId}` - Get user profile
- `PUT /users/updateProfile` - Update profile
- `POST /users/upload-profile-picture` - Upload profile picture
- `POST /users/follow/{targetUserId}` - Follow user
- `DELETE /users/unfollow/{targetUserId}` - Unfollow user

### Article Endpoints
- `POST /articles/createSagesseArticla` - Create wisdom article
- `POST /articles/createHistoireArticla` - Create story article
- `GET /articles/posts/{currentUserId}` - Get personalized feed
- `POST /articles/{id}/like` - Like article
- `POST /articles/{id}/unlike` - Unlike article
- `GET /articles/search/{currentUserId}` - Search articles

### Comment Endpoints
- `POST /comments/add/{articleId}` - Add comment
- `GET /comments/article/{articleId}` - Get article comments
- `DELETE /comments/delete/{commentId}` - Delete comment


## 📦 Building for Production

### Production Build
```bash
npm run build
```

### Build Optimizations
- Code splitting for optimal loading
- Tree shaking to remove unused code
- Asset optimization (images, fonts)
- CSS minification
- JavaScript minification
- Lazy loading of components

### Performance Metrics
- Lighthouse score optimization
- Core Web Vitals monitoring
- Bundle size analysis

## 🐳 Docker Deployment

### Create Dockerfile
```dockerfile
# Build stage
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### nginx.conf
```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Build and Run Docker Container
```bash
# Build image
docker build -t voix-de-sagesse-frontend .

# Run container
docker run -p 80:80 voix-de-sagesse-frontend
```

### Docker Compose
```yaml
version: '3.8'
services:
  frontend:
    build: .
    ports:
      - "80:80"
    environment:
      - VITE_API_BASE_URL=http://backend:8080
    depends_on:
      - backend
```

## 🔧 Configuration

### Vite Configuration
```javascript
// vite.config.js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom', 'react-router-dom'],
          redux: ['@reduxjs/toolkit', 'react-redux'],
          ui: ['@mantine/core', '@mui/material'],
        },
      },
    },
  },
});
```

### Tailwind Configuration
```javascript
// tailwind.config.js
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#3B82F6',
        secondary: '#10B981',
        accent: '#F59E0B',
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
```

## 🐛 Troubleshooting

### Common Issues

**API Connection Errors**
```
Solution: Ensure backend is running on http://localhost:8080
- Check VITE_API_BASE_URL in .env file
- Verify CORS configuration in backend
- Check network tab for failed requests
```

**Redux State Not Persisting**
```
Solution: Check localStorage functionality
- Verify browser localStorage is enabled
- Clear localStorage and refresh: localStorage.clear()
- Check Redux DevTools for state changes
```

**Build Errors**
```
Solution: Clear cache and reinstall dependencies
npm run clean
rm -rf node_modules package-lock.json
npm install
npm run build
```

**Routing Issues**
```
Solution: Configure server for SPA routing
- Ensure server redirects all routes to index.html
- Check React Router configuration
- Verify protected route logic
```

## 🎯 Performance Optimization

### Code Splitting
```javascript
// Lazy load components
const AdminDashboard = lazy(() => import('./pages/AdminDashboard'));
const ProfilePage = lazy(() => import('./pages/ProfilePage'));
```

### Image Optimization
- Use WebP format for images
- Implement lazy loading for images
- Optimize image sizes for different devices

### Bundle Size Reduction
- Analyze bundle with `npm run build -- --analyze`
- Remove unused dependencies
- Use tree shaking for libraries

## 🤝 Backend Integration

This frontend application is designed to work with the VoixDeSagesse backend API.

**Backend Repository**: [VoixDeSagesse Backend](https://github.com/Oumaima-Lg/Voix-De-Sagesse-Backend)

The backend provides:
- Spring Boot 3.4.4 REST API
- JWT authentication
- MongoDB database
- Email services with OTP
- File upload support
- Admin moderation tools

## 📱 Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)
- Mobile browsers (iOS Safari, Chrome Mobile)

## 🔐 Security Features

- XSS protection with React's built-in sanitization
- CSRF protection via JWT tokens
- Secure HTTP-only cookie support
- Input validation on all forms
- Protected routes with authentication checks
- Automatic token refresh mechanism
- Secure password requirements enforcement

## 📄 License

This project is part of an academic end-of-year project at **Hassan I University - Faculty of Sciences and Techniques of Settat**, Department of Computer Engineering.

**Academic Year**: 2024-2025  
**Author**: Oumaima LAGHJIBI

## 👥 Contact & Support

For questions, issues, or contributions:

- **Email**: laghjibioumaima2003@gmail.com
- **GitHub Issues**: [Report an issue](https://github.com/Oumaima-Lg/Voix-De-Sagesse-Frontend/issues)
- **Backend Repository**: [Voix-De-Sagesse-Backend](https://github.com/Oumaima-Lg/Voix-De-Sagesse-Backend)

## 🙏 Acknowledgments

- React Team for the amazing framework
- Vite for the blazing-fast build tool
- Tailwind CSS for the utility-first CSS framework
- Redux Toolkit team for simplified state management
- All open-source contributors

---

<div align="center">

**Built with ❤️ using React and modern web technologies**

⭐ Star this repository if you find it helpful!

</div>
