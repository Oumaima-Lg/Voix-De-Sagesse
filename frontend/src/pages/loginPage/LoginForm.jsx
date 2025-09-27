import React from 'react';
import styled from 'styled-components';
import { useState } from 'react';
import { Link as LinkRouter, useLocation } from "react-router-dom";
import { useNavigate } from 'react-router-dom';
import { loginValidation } from '../../services/FormValidation';
import { TextInput } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import ResetPassword from './ResetPassword';
import { errorNotification, successNotification } from '../../services/NotificationService';
import { useDispatch } from 'react-redux';
import { setUser } from '../../Slices/UserSlice';
import { LoadingOverlay, Button, Group, Box } from '@mantine/core';
import { setJwt } from '../../Slices/JwtSlice';
import { loginUser } from '../../services/AuthService';
import { jwtDecode } from "jwt-decode";
import { useAuth } from '../../hooks/useAuth';
import { useEffect } from 'react';

const LoginForm = () => {
    const [loading, setLoading] = useState(false);
    const [visible, { toggle }] = useDisclosure(true);
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

        console.log("data recu : ", data);

        if (valid) {
            setLoading(true);

            loginUser(data)
                .then((res) => {
                    console.log('Login success response:', res);

                    try {
                        clearAuth();

                        const decoded = jwtDecode(res.jwt);
                        console.log('Decoded JWT:', decoded);

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
                                console.log('Redirecting admin user to:', redirectTo);
                            } else {
                                const redirectParam = new URLSearchParams(location.search).get('redirect');
                                const from = location.state?.from?.pathname;
                                redirectTo = redirectParam || from || '/articla';
                                console.log('Redirecting regular user to:', redirectTo);
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

                    const errorMessage = err.response?.data?.errorMessage ||
                        err.response?.data?.message ||
                        err.message ||
                        'Erreur de connexion';

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
            <StyledWrapper>
                <div className="mx-auto p-14">
                    <div className='card mx-auto border-4 border-image-gradient sm:px-9 sm:py-4 px-4 py-2 flex flex-col md:gap-10 sm:gap-8 gap-6'>
                        <h4 className="text-gradient lg:text-4xl sm:text-3xl text-xl text-center lg:px-20 px-10 pt-2">Se Connecter</h4>

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

                        <form className='flex flex-col md:gap-4 gap-2'>
                            <TextInput
                                error={formError.email}
                                size='lg'
                                placeholder="Email"
                                value={data.email}
                                onChange={handleChange}
                                name="email"
                                disabled={loading} 
                            />
                            <TextInput
                                error={formError.password}
                                size='lg'
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
                                    className="btn btn-border-gradient text-amber-50! mb-6!"
                                    onClick={handleSubmit}
                                    disabled={loading}
                                    type="submit" 
                                >
                                    {loading ? 'CONNEXION...' : 'CONNEXION'}
                                </button>

                                <div
                                    onClick={loading ? undefined : open} 
                                    className={`block mb-6! border-b-1 max-w-max mx-auto ${loading ? 'cursor-not-allowed opacity-50' : 'cursor-pointer'}`}
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
            </StyledWrapper>

            <div>
                <ResetPassword opened={opened} close={close} />
            </div>
        </>
    );
}

const StyledWrapper = styled.div`
  .card {
    box-shadow: 
    8px 8px 20px -5px rgba(76, 49, 99, 0.4),
    -5px -5px 15px rgba(160, 159, 135, 0.2),
    inset 2px 2px 5px rgba(255, 255, 255, 0.1);
   text-align: center;
  }

  .input-icon {
   height: 1em;
   width: 1em;
  }

  /*Buttons*/
  .btn {
   margin: 1rem;
   border: none;
   border-radius: 4px;
   font-weight: bold;
   font-size: .8em;
   text-transform: uppercase;
   padding: 0.6em 1.2em;
   box-shadow: 0 8px 24px 0 rgb(255 235 167 / 20%);
   transition: all .3s ease-in-out;
  }

  .btn:disabled {
   opacity: 0.6;
   cursor: not-allowed;
  }

  .btn-link {
   display: block;
   font-size: .75em;
   transition: color .3s ease-out;
  }

  /*Hover & focus*/
  input:focus::placeholder {
   opacity: 0;
   transition: opacity .3s;
  }

  .btn:hover:not(:disabled) {
   box-shadow: 0 8px 24px 0 rgb(16 39 112 / 20%);
  }
`;

export default LoginForm;

