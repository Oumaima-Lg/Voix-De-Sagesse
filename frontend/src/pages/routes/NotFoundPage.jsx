import { useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, Home, Search, AlertTriangle } from 'lucide-react';

const NotFoundPage = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleGoBack = () => {
    window.history.length > 2 ? navigate(-1) : navigate('/');
  };

  const handleGoHome = () => {
    navigate('/');
  };

  const handleGoToArticles = () => {
    navigate('/articla');
  };

  return (
    <div className="min-h-screen bg-gradient-to-bl from-[#171717] from-55% to-[#4E3F59] to-100% flex items-center justify-center p-4">
      <div className="max-w-2xl mx-auto text-center">
        {/* Icône et titre principal */}
        <div className="mb-8">
          <div className="relative mb-6">
            <div className="w-32 h-32 mx-auto bg-gradient-to-br from-[#A09F87] to-[#4C3163] rounded-full flex items-center justify-center animate-pulse">
              <AlertTriangle className="w-16 h-16 text-white" />
            </div>
            <div className="absolute -top-1 -right-8 w-8 h-8 rounded-full animate-bounce">
                <AlertTriangle className="w-4 h-4 text-white" />
            </div>
          </div>
          
          <h1 className="text-6xl md:text-8xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-[#A09F87] to-white mb-4">
            404
          </h1>
          
          <h2 className="text-2xl md:text-3xl font-semibold text-white mb-2">
            Page Introuvable
          </h2>
          
          <p className="text-[#A09F87] text-lg mb-2">
            La page que vous recherchez n'existe pas ou a été déplacée.
          </p>
          
          <div className="bg-[#202020] border border-[#4C3163] rounded-lg p-3 mb-8">
            <p className="text-gray-400 text-sm">
              <span className="font-medium">URL demandée:</span>
              <br />
              <code className="text-[#A09F87]">{location.pathname}</code>
            </p>
          </div>
        </div>

        {/* Suggestions et actions */}
        <div className="space-y-4 mb-8">
          <h3 className="text-xl font-semibold text-white mb-4">
            Que souhaitez-vous faire ?
          </h3>
          
          <div className="grid md:grid-cols-3 gap-4">
            <button
              onClick={handleGoBack}
              className="bg-gradient-to-r from-[#4C3163] to-[#A09F87] hover:from-[#A09F87] hover:to-[#4C3163] text-white font-medium py-3 px-6 rounded-lg transition-all duration-300 flex items-center justify-center gap-2 transform hover:scale-105"
            >
              <ArrowLeft className="w-5 h-5" />
              Retour
            </button>
            
            <button
              onClick={handleGoHome}
              className="bg-gradient-to-r from-[#A09F87] to-[#4C3163] hover:from-[#4C3163] hover:to-[#A09F87] text-white font-medium py-3 px-6 rounded-lg transition-all duration-300 flex items-center justify-center gap-2 transform hover:scale-105"
            >
              <Home className="w-5 h-5" />
              Accueil
            </button>
            
            <button
              onClick={handleGoToArticles}
              className="border-2 border-[#A09F87] text-[#A09F87] hover:bg-[#A09F87] hover:text-[#171717] font-medium py-3 px-6 rounded-lg transition-all duration-300 flex items-center justify-center gap-2 transform hover:scale-105"
            >
              <Search className="w-5 h-5" />
              Articles
            </button>
          </div>
        </div>

        {/* Pages populaires */}
        <div className="bg-[#202020] border border-[#4C3163] rounded-lg p-6">
          <h4 className="text-lg font-semibold text-white mb-4">
            Pages populaires
          </h4>
          
          <div className="grid md:grid-cols-2 gap-3">
            {[
              { name: "Mes Postes", path: "/articla/my-posts", icon: "✍️" },
              { name: "Articla", path: "/articla", icon: "📝" },
              { name: "Profil", path: "/articla/profile", icon: "👤" },
              { name: "Favoris", path: "/saved-articles", icon: "⭐" }
            ].map((item) => (
              <button
                key={item.path}
                onClick={() => navigate(item.path)}
                className="text-left p-3 rounded hover:bg-[#4C3163] text-gray-300 hover:text-white transition-colors group"
              >
                <span className="text-xl mr-3 group-hover:scale-110 transition-transform inline-block">
                  {item.icon}
                </span>
                {item.name}
              </button>
            ))}
          </div>
        </div>

        {/* Message d'aide */}
        <div className="mt-8 text-center">
          <p className="text-gray-400 text-sm">
            Si vous pensez qu'il s'agit d'une erreur, veuillez{' '}
            <a href="#" className="text-[#A09F87] hover:text-white underline">
              nous contacter
            </a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default NotFoundPage;