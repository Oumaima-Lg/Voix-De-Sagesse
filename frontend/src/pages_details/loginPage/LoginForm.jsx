import React from 'react';
import styled from 'styled-components';
import { useState } from 'react';
import { loginUser } from '../../services/UserService';
import { Link as LinkRouter } from "react-router-dom";
import { useNavigate } from 'react-router-dom';
import { loginValidation } from '../../services/FormValidation';
import { TextInput } from '@mantine/core';
import { notifications } from '@mantine/notifications';
import { IconCheck, IconX } from '@tabler/icons-react';
import { useDisclosure } from '@mantine/hooks';
import ResetPassword from './ResetPassword';
import { errorNotification, successNotification } from '../../services/NotificationService';
import { useDispatch } from 'react-redux';
import { setUser } from '../../Slices/UserSlice';
import { LoadingOverlay, Button, Group, Box } from '@mantine/core';


// log1@45HG



const LoginForm = () => {
    const [loading, setLoading] = useState(false);
    const [visible, { toggle }] = useDisclosure(true);
    const dispatch = useDispatch();
    const form = {
        email: "",
        motdepasse: "",
    }

    const [data, setData] = useState(form);
    const [formError, setFormError] = useState(form);
    const [opened, { open, close }] = useDisclosure(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        // console.log(e.target);
        setFormError({ ...formError, [e.target.name]: "" });
        setData({ ...data, [e.target.name]: e.target.value });
    }

    const handleSubmit = (e) => {
       
        let valid = true;
        let newFormError = {};
        for (let key in data) {
            newFormError[key] = loginValidation(key, data[key]);

            if (newFormError[key]) valid = false;
        }
        setFormError(newFormError);
        e.preventDefault(); //  pour empêcher le comportement par défaut du formulaire
        console.log("data recu : ", data);
        if (valid) {
            setLoading(true);
            loginUser(data).then((res) => {
                console.log(res);
                // alert('Login réussie!');
                successNotification('Success', 'Login réussie !');
                setTimeout(() => {
                    setLoading(false);
                    dispatch(setUser(res));
                    navigate("/articla");
                }, 4000)
            }).catch((err) => {
                setLoading(false);
                console.log(err.response.data);
                errorNotification('Failed', err.response.data.errorMessage);
            });
        }
    }

    return (
        <>

            <StyledWrapper>
                <div className="mx-auto  p-14">
                    <div className='card mx-auto border-4 border-image-gradient sm:px-9 sm:py-4 px-4 py-2 flex flex-col md:gap-10 sm:gap-8 gap-6'>
                        <h4 className="text-gradient lg:text-4xl sm:text-3xl text-xl text-center lg:px-20 px-10 pt-2">Se Connecter</h4>
                        <form className='flex flex-col md:gap-4 gap-2'>
                            <TextInput
                                error={formError.email}
                                size='lg'
                                placeholder="Email"
                                value={data.email}
                                onChange={handleChange}
                                name="email"
                            />
                            <TextInput
                                error={formError.motdepasse}
                                size='lg'
                                placeholder="Mot de passe"
                                value={data.motdepasse}
                                onChange={handleChange}
                                name="motdepasse"
                            />

                            <div>
                                <LoadingOverlay
                                    visible={loading}
                                    zIndex={1000}
                                    overlayProps={{ radius: 'sm', blur: 2 }}
                                    loaderProps={{ color: '#A09F87', type: 'bars' }}
                                />
                                <button className="btn btn-border-gradient text-amber-50! mb-6!" onClick={handleSubmit}>CONNEXION</button>
                                <div onClick={open} className="block mb-6! border-b-1 max-w-max mx-auto cursor-pointer">Mot de passe oublié ?</div>
                                <div className='h-0.5 bg-gradient-to-br from-[#4C3163] to-[#A09F87]' />
                                <button className="btn btn-border-gradient text-amber-50! cursor-pointer" onClick={(e) => { e.preventDefault(); navigate("/auth/register"); setFormError(form); setData(form) }}  >
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
//    fill: #ffeba7;
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
//    background-color: #ffeba7;
//    color: #5e6681;
   box-shadow: 0 8px 24px 0 rgb(255 235 167 / 20%);
   transition: all .3s ease-in-out;
  }

  .btn-link {
//    color: #f5f5f5;
   display: block;
   font-size: .75em;
   transition: color .3s ease-out;
  }

  /*Hover & focus*/
  input:focus::placeholder {
   opacity: 0;
   transition: opacity .3s;
  }

  .btn:hover {
//    background-color: #5e6681;
//    color: #ffeba7;
   box-shadow: 0 8px 24px 0 rgb(16 39 112 / 20%);
  }

  .btn-link:hover {
//    color: #ffeba7;
  }`;

export default LoginForm;
