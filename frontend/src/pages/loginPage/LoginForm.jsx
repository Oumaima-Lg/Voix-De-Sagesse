import React from 'react';
import { useState } from 'react';
import { useLocation } from "react-router-dom";
import { useNavigate } from 'react-router-dom';
import { loginValidation } from '../../utils/formValidation';
import { TextInput } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import ResetPassword from './ResetPassword';
import { errorNotification, successNotification } from '../../utils/notifications';
import { useDispatch } from 'react-redux';
import { setUser } from '../../store/userSlice';
import { LoadingOverlay } from '@mantine/core';
import { setJwt } from '../../store/jwtSlice';
import { loginUser } from '../../api/authService';
import { jwtDecode } from "jwt-decode";
import { useAuth } from '../../hooks/useAuth';
import { useEffect } from 'react';
import './LoginForm.css';

const LoginForm = () => {
    const [loading, setLoading] = useState(false);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const location = useLocation(); 
    const { clearAuth } = useAuth(); 
    const [urlMessage, setUrlMessage] = useState('');
    const [isFromLogout, setIsFromLogout] = useState(false);

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const message = params.get('message');
        const fromLogoutParam = params.get('fromLogout');

        if (message) {
            setUrlMessage(decodeURIComponent(message));
            window.history.replaceState({}, '', location.pathname);
        }
        if (fromLogoutParam === 'true') {
            setIsFromLogout(true);
        } else {
            setIsFromLogout(false);
        }
    }, [location]);

    const form = {
        email: "",
        password: "",
    }

    const [data, setData] = useState(form);
    const [formError, setFormError] = useState(form);
    const [opened, { open, close }] = useDisclosure(false);

    const handleChange = (e) => {
        setFormError({ ...formError, [e.target.name]: "" });
        setData({ ...data, [e.target.name]: e.target.value });
    }

    const handleSubmit = (e) => {
        e.preventDefault(); 

        let valid = true;
        let newFormError = {};
        for (let key in data) {
            newFormError[key] = loginValidation(key, data[key]);
            if (newFormError[key]) valid = false;
        }
        setFormError(newFormError);

        if (valid) {
            setLoading(true);

            loginUser(data)
                .then((res) => {

                    try {
                        clearAuth();

                        const decoded = jwtDecode(res.jwt);

                        const userData = {
                            ...decoded,
                            email: decoded.sub,
                            accountType: decoded.accountType,
                            ...(res.user || {})
                        };

                        localStorage.setItem('token', res.jwt);
                        localStorage.setItem('user', JSON.stringify(userData));

                        dispatch(setJwt(res.jwt));
                        dispatch(setUser(userData));
                        successNotification('Succès', 'Connexion réussie !');
                        setTimeout(() => {
                            setLoading(false);
                            let redirectTo;

                            if (userData.accountType === 'ADMIN') {
                                redirectTo = '/admin/dashboard';
                            } else {
                                const redirectParam = new URLSearchParams(location.search).get('redirect');
                                const from = location.state?.from?.pathname;
                                redirectTo = redirectParam || from || '/espace';
                            }

                            navigate(redirectTo, { replace: true });
                        }, 2000);
                    } catch (error) {
                        console.error('Error processing login response:', error);
                        setLoading(false);
                        clearAuth(); 
                        errorNotification('Erreur', 'Problème lors du traitement de la connexion');
                    }
                })
                .catch((err) => {
                    setLoading(false);
                    console.error('Login error:', err);

                    const errorMessage = !err.response
                        ? 'Impossible de contacter le serveur. Réessayez plus tard.'
                        : err.response.status === 401
                            ? 'Email ou mot de passe incorrect.'
                            : err.response.data?.errorMessage || err.response.data?.message || 'Erreur de connexion.';

                    errorNotification('Échec de la connexion', errorMessage);
                    clearAuth();
                });
        }
    }

    const handleNavigateToRegister = (e) => {
        e.preventDefault();
        setFormError(form);
        setData(form);
        clearAuth();
        navigate("/auth/register");
    }

    return (
        <>
            <div className='login-form'>
                <div className="mx-auto px-4">
                    <div className='card w-[min(92vw,26rem)] mx-auto border-4 border-image-gradient sm:px-8 px-4 py-4 flex flex-col gap-4'>
                        <h4 className="text-gradient sm:text-3xl text-2xl text-center pt-2">Se Connecter</h4>

                        {urlMessage && (
                            <div className={`text-center text-sm mb-4 p-3 rounded border ${isFromLogout
                                ? 'text-green-400 bg-green-400/10 border-green-400/20' 
                                : 'text-yellow-400 bg-yellow-400/10 border-yellow-400/20' 
                                }`}>
                                <div className="flex items-center justify-center gap-2">
                                    <span>
                                        {isFromLogout ? '✅' : '⚠️'}
                                    </span>
                                    <span>{urlMessage}</span>
                                </div>
                            </div>
                        )}

                        <form className='flex flex-col gap-3'>
                            <TextInput
                                error={formError.email}
                                size='md'
                                placeholder="Email"
                                value={data.email}
                                onChange={handleChange}
                                name="email"
                                disabled={loading} 
                            />
                            <TextInput
                                error={formError.password}
                                size='md'
                                placeholder="Mot de passe"
                                type="password"
                                value={data.password}
                                onChange={handleChange}
                                name="password"
                                disabled={loading}
                            />

                            <div>
                                <LoadingOverlay
                                    visible={loading}
                                    zIndex={1000}
                                    overlayProps={{ radius: 'sm', blur: 2 }}
                                    loaderProps={{ color: '#A09F87', type: 'bars' }}
                                />

                                <button
                                    className="btn btn-border-gradient text-amber-50! mb-3!"
                                    onClick={handleSubmit}
                                    disabled={loading}
                                    type="submit" 
                                >
                                    {loading ? 'CONNEXION...' : 'CONNEXION'}
                                </button>

                                <div
                                    onClick={loading ? undefined : open} 
                                    className={`block mb-3! border-b-1 max-w-max mx-auto ${loading ? 'cursor-not-allowed opacity-50' : 'cursor-pointer'}`}
                                >
                                    Mot de passe oublié ?
                                </div>

                                <div className='h-0.5 bg-gradient-to-br from-[#4C3163] to-[#A09F87]' />

                                <button
                                    className="btn btn-border-gradient text-amber-50! cursor-pointer"
                                    onClick={handleNavigateToRegister}
                                    disabled={loading} 
                                    type="button"
                                >
                                    Créer un nouveau compte
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <div>
                <ResetPassword opened={opened} close={close} />
            </div>
        </>
    );
}

export default LoginForm;

