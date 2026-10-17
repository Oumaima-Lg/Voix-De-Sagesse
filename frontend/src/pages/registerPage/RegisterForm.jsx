import React from 'react';
import styled from 'styled-components';
import { useState } from 'react';
import { registerUser } from '../../services/AuthService';
import { signupValidation } from '../../services/FormValidation';
import { TextInput } from '@mantine/core';
import { useNavigate } from 'react-router-dom';
import { errorNotification, successNotification } from '../../services/NotificationService';

const form = {
    nom: "",
    prenom: "",
    email: "",
    motdepasse: "",
    confirmerMotdepasse: "",
    accountType: "USER"
}

const RegisterForm = () => {

    const [data, setData] = useState(form);
    const [formError, setFormError] = useState(form);
    const navigate = useNavigate();


    const handleChange = (e) => {
        const { name, value } = e.target;
        const newData = { ...data, [name]: value };
        setData(newData);

        const newErrors = { ...formError, [name]: signupValidation(name, value) };

        if (name === "motdepasse" && newData.confirmerMotdepasse !== "") {
            newErrors.confirmerMotdepasse =
                newData.confirmerMotdepasse !== value ?
                    "Les mots de passe ne correspondent pas." : "";
        }

        if (name === "confirmerMotdepasse") {
            newErrors.confirmerMotdepasse =
                newData.motdepasse !== value ?
                    "Les mots de passe ne correspondent pas." : "";
        }

        setFormError(newErrors);
    };


    const handleSubmit = (e) => {

        let valid = true;
        let newFormError = {};

        for (let key in data) {
            if (key === "accountType") continue;

            if (key === "confirmerMotdepasse") {
                if (data[key] !== data["motdepasse"]) {
                    newFormError[key] = "Les mots de passe ne correspondent pas.";
                }
            } else {
                newFormError[key] = signupValidation(key, data[key]);
            }

            if (newFormError[key]) valid = false;
        }

        setFormError(newFormError);

        e.preventDefault();

        if (valid === true) {
            registerUser(data).then(() => {
                setData(form);
                successNotification('Succès', 'Inscription réussie !');
                
                setTimeout( () => {
                    navigate("/auth/login")
                }, 2000 )

            }).catch((err) => {
                errorNotification("Échec de l'inscription", err.response
                    ? err.response.data?.errorMessage || "Erreur lors de l'inscription."
                    : 'Impossible de contacter le serveur. Réessayez plus tard.');
                
            });
        }
    }



    return (
        <StyledWrapper>
            <div className="mx-auto px-4">
                <div className='card w-[min(92vw,26rem)] mx-auto border-4 border-image-gradient sm:px-8 px-4 py-4 flex flex-col gap-4'>
                    <h4 className="text-gradient sm:text-3xl text-2xl text-center pt-2">Créer un compte</h4>
                    <form className='flex flex-col gap-3'>
                        <div className='grid sm:grid-cols-2 gap-3'>
                            <TextInput error={formError.prenom} size='md' placeholder="Prénom" value={data.prenom} onChange={handleChange} name="prenom" />
                            <TextInput
                                error={formError.nom}
                                size='md'
                                placeholder="Nom de famille"
                                value={data.nom}
                                onChange={handleChange}
                                name="nom"
                            />
                        </div>
                        <TextInput
                            error={formError.email}
                            size='md'
                            placeholder="Email"
                            value={data.email}
                            onChange={handleChange}
                            name="email"
                        />
                        <TextInput
                            error={formError.motdepasse}
                            size='md'
                            placeholder="Mot de passe"
                            type="password"
                            value={data.motdepasse}
                            onChange={handleChange}
                            name="motdepasse"
                        />
                        <TextInput
                            error={formError.confirmerMotdepasse}
                            size='md'
                            placeholder="Confirmez le mot de passe"
                            type="password"
                            value={data.confirmerMotdepasse}
                            onChange={handleChange}
                            name="confirmerMotdepasse"
                        />
                        <div>
                            <button className="btn btn-border-gradient text-amber-50! mb-3!" onClick={handleSubmit} >S'inscrire</button>
                            <div className='h-0.5 mb-3 bg-gradient-to-br from-[#4C3163] to-[#A09F87]' />
                            <span className="block mb-3! border-b-1 max-w-max mx-auto cursor-pointer" onClick={ () => {navigate("/auth/login"); setFormError(form); setData(form)} }>Vous avez déjà un compte ?</span>
                        </div>

                    </form>
                </div>

            </div>
        </StyledWrapper>
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
   margin: 0.75rem;
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

export default RegisterForm;
